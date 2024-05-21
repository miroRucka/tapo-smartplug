package online.labmaster.taposmartplug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import online.labmaster.taposmartplug.api.inbound.HandshakeResponse;
import online.labmaster.taposmartplug.api.inbound.LoginResponse;
import online.labmaster.taposmartplug.api.outbound.LoginRequest;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.client.TapoKeys;
import online.labmaster.taposmartplug.exception.TapoAuthException;
import online.labmaster.taposmartplug.exception.TapoUnavailableException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.CookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.byteArrayToInt;
import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.calcAuthHash;
import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.concat;
import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.generateRandomBytes;

@Service
@Slf4j
public class TapoKeysService {

    @Autowired
    private TapoClient tapoClient;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${tapo.plug.username}")
    private String username;

    @Value("${tapo.plug.password}")
    private String password;

    @Value("${tapo.plug.IPs}")
    private List<String> plugIPs;

    private HashMap<String, TapoKeys> tapoKeys;

    @PostConstruct
    public void init() {
        if (plugIPs == null || plugIPs.isEmpty()) {
            throw new IllegalStateException("No smart plug registered, please add parameter or property tapo.plug.IPs=ip plug1,ip plug2, ip...");
        }
        try {
            tapoKeys = loadAllKeys();
        } catch (Exception e) {
            log.error("cannot retrieve energy usage", e);
        }
    }

    public void invalidateAndReloadKeys(String plugIP) {
        tapoKeys.remove(plugIP);
        try {
            tapoKeys.put(plugIP, loadKlapKeys(plugIP));
        } catch (Exception e) {
            log.warn("Failed to use KLAP protocol, fallback to passthrough: {}", plugIP);
            tapoKeys.put(plugIP, loadKeys(plugIP));
        }
    }

    private HashMap<String, TapoKeys> loadAllKeys() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        HashMap<String, TapoKeys> result = new HashMap<>();
        for (String plugIP : plugIPs) {
            // Try to load keys with KLAP. If fails, fallback to old protocol.
            try {
                result.put(plugIP, loadKlapKeys(plugIP));
            } catch (Exception e) {
                log.warn("Failed to use KLAP protocol, fallback to passthrough: {}", plugIP);
                result.put(plugIP, loadKeys(plugIP));
            }
        }
        return result;
    }

    private TapoKeys loadKeys(String plugIP) {
        try {
            CookieStore cookieStore = tapoClient.getCookieStore();
            KeyPair keyPair = encryptionService.generateKeyPair();
            //handshake
            String publicKey = encryptionService.transformPublicCertificate(keyPair.getPublic());
            HandshakeResponse handshakeResponse = tapoClient.callHandshake(plugIP, publicKey, cookieStore);
            //login
            byte[] keys = encryptionService.decryptKeys(keyPair.getPrivate(), handshakeResponse.result.key);
            String encryptedLoginRequest = encryptionService.encryptMessage(keys, objectMapper.writeValueAsString(new LoginRequest(encryptionService.encryptLoginName(username), encryptionService.base64Encode(password))));
            LoginResponse loginResponse = tapoClient.callEncrypted(plugIP, encryptedLoginRequest, cookieStore, null, LoginResponse.class, keys);
            return new TapoKeys(loginResponse.result.token, keys, cookieStore);
        } catch (InvalidAlgorithmParameterException |
                 InvalidKeyException |
                 BadPaddingException |
                 NoSuchPaddingException |
                 NoSuchAlgorithmException |
                 IllegalBlockSizeException e) {
            log.error("Error while processing keys for {}", plugIP);
            throw new TapoAuthException("Authentication failed for {}" + plugIP);
        } catch (IOException e) {
            log.error("Tapo Device is not available: {}", plugIP);
            throw new TapoUnavailableException("Tapo device unavailable: " + plugIP);
        }
    }

    private TapoKeys loadKlapKeys(String plugIP) throws IOException {
        String url = "http://" + plugIP + "/app";
        CookieStore cookieStore = tapoClient.getCookieStore();

        // KLAP Handshake 1
        byte[] localSeed = generateRandomBytes(16);
        byte[] authHash = calcAuthHash(username, password);
        byte[] remoteSeed = tapoClient.callKlapHandshake1(url, localSeed, authHash, cookieStore);

        // KLAP Handshake 2
        tapoClient.callKlapHandshake2(url, localSeed, remoteSeed, authHash, cookieStore);

        byte[] key = Arrays.copyOfRange(DigestUtils.sha256(concat("lsk".getBytes(), localSeed, remoteSeed, authHash)), 0, 16);
        byte[] ivSeq = DigestUtils.sha256(concat("iv".getBytes(), localSeed, remoteSeed, authHash));
        byte[] iv = Arrays.copyOfRange(ivSeq, 0, 12);
        int seq = byteArrayToInt(Arrays.copyOfRange(ivSeq, 12, 16));
        byte[] sig = Arrays.copyOfRange(DigestUtils.sha256(concat("ldk".getBytes(), localSeed, remoteSeed, authHash)), 0, 28);

        return new TapoKeys(key, ivSeq, iv, seq, sig, cookieStore);
    }

    public TapoKeys getTapoKeys(String plugIP) {
        if (tapoKeys != null && tapoKeys.get(plugIP) != null) {
            return tapoKeys.get(plugIP);
        }
        init();
        return tapoKeys.get(plugIP);
    }
}
