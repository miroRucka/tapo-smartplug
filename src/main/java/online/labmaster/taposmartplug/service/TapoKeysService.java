package online.labmaster.taposmartplug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.HandshakeResponse;
import online.labmaster.taposmartplug.api.inbound.LoginResponse;
import online.labmaster.taposmartplug.api.outbound.LoginRequest;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.client.TapoKeys;
import online.labmaster.taposmartplug.metrics.TapoMetrics;
import org.apache.http.client.CookieStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.HashMap;
import java.util.List;

@Service
public class TapoKeysService {

    public static final Logger logger = LoggerFactory.getLogger(TapoKeysService.class);

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
            logger.error("cannot retrieve energy usage", e);
        }
    }

    private HashMap<String, TapoKeys> loadAllKeys() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        HashMap<String, TapoKeys> result = new HashMap<>();
        for (String plugIP : plugIPs) {
            result.put(plugIP, loadKeys(plugIP));
        }
        return result;
    }

    private TapoKeys loadKeys(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
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
    }

    public TapoKeys getTapoKeys(String plugIP) {
        if (tapoKeys != null && tapoKeys.get(plugIP) != null) {
            return tapoKeys.get(plugIP);
        }
        init();
        return tapoKeys.get(plugIP);
    }
}
