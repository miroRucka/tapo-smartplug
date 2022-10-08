package online.labmaster.taposmartplug;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;
import online.labmaster.taposmartplug.api.inbound.LoginResponse;
import online.labmaster.taposmartplug.api.outbound.EnergyUsageRequest;
import online.labmaster.taposmartplug.api.outbound.LoginRequest;
import online.labmaster.taposmartplug.api.inbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.inbound.HandshakeResponse;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.client.TapoKeys;
import online.labmaster.taposmartplug.encryption.EncryptionService;
import org.apache.http.client.CookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api")
public class TestController {

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

    @Value("${tapo.plug.terminal.id}")
    private String terminalId;

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity test() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        loadKeys();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/energy-usage", method = RequestMethod.GET)
    public EnergyUsageResponse energyUsed() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = loadKeys();
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new EnergyUsageRequest(terminalId)));
        return tapoClient.callEncrypted(encryptedRequest, keys.getCookieStore(), keys.getToken(), EnergyUsageResponse.class, keys.getKeys());
    }

    private TapoKeys loadKeys() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        CookieStore cookieStore = tapoClient.getCookieStore();
        KeyPair keyPair = encryptionService.generateKeyPair();

        //handshake
        String publicKey = encryptionService.transformPublicCertificate(keyPair.getPublic());
        HandshakeResponse handshakeResponse = tapoClient.callHandshake(publicKey, cookieStore);

        //login
        byte[] keys = encryptionService.decryptKeys(keyPair.getPrivate(), handshakeResponse.result.key);
        String encryptedLoginRequest = encryptionService.encryptMessage(keys, objectMapper.writeValueAsString(new LoginRequest(encryptionService.encryptLoginName(username), encryptionService.base64Encode(password))));
        LoginResponse loginResponse = tapoClient.callEncrypted(encryptedLoginRequest, cookieStore, null, LoginResponse.class, keys);
        return new TapoKeys(loginResponse.result.token, keys, cookieStore);
    }
}
