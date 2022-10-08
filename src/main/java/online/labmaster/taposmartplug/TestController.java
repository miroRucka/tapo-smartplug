package online.labmaster.taposmartplug;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.LoginRequest;
import online.labmaster.taposmartplug.api.outbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.outbound.HandshakeResponse;
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

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity test() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {

        System.out.println("----------------> " + loadKeys());

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    private TapoKeys loadKeys() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        CookieStore cookieStore = tapoClient.getCookieStore();
        KeyPair keyPair = encryptionService.generateKeyPair();

        //handshake
        String publicKey = encryptionService.transformPublicCertificate(keyPair.getPublic());
        HandshakeResponse handshakeResponse = tapoClient.callHandshake(publicKey, cookieStore);

        //login
        byte[] keys = encryptionService.decryptKeys(keyPair.getPrivate(), handshakeResponse.getResult().getKey());
        LoginRequest loginRequest = new LoginRequest(LoginRequest.LOGIN_DEVICE_METHOD, encryptionService.encryptLoginName(username), encryptionService.base64Encode(password));
        String rq = encryptionService.encryptMessage(keys, objectMapper.writeValueAsString(loginRequest));
        EnvelopeResponse loginResponse = tapoClient.callEncrypted(rq, cookieStore);
        return new TapoKeys(encryptionService.decryptMessage(keys, loginResponse.getResult().getResponse()), keys, cookieStore);
    }
}
