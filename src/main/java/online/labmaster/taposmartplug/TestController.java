package online.labmaster.taposmartplug;

import com.fasterxml.jackson.core.JsonProcessingException;
import online.labmaster.taposmartplug.api.outbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.outbound.HandshakeResponse;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.encryption.EncryptionService;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.tomcat.util.net.openssl.ciphers.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    @RequestMapping(path = "/test", method = RequestMethod.GET)
    public ResponseEntity test() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        CookieStore cookieStore = tapoClient.getCookieStore();
        KeyPair keyPair = encryptionService.generateKeyPair();
        //handshake
        String publicKey = encryptionService.transformPublicCertificate(keyPair.getPublic());
        HandshakeResponse handshakeResponse = tapoClient.callHandshake(publicKey, cookieStore);

        //login
        System.out.println("-----> " + handshakeResponse.getResult().getKey());
        byte[] keys = encryptionService.decryptKeys(keyPair.getPrivate(), handshakeResponse.getResult().getKey());
        String loginRequest = encryptionService.encryptMessage(keys, "{\"method\": \"login_device\", \"params\": {\"username\": \"OTgwYWM5ZDA4YjI1OGE1MmZjYjJmODFmMzMyZWE0Yjk1ZDY3ZmMwYQ==\", \"password\": \"TTdxamk4bms3NkV5VzI=\"}, \"requestTimeMils\": 0}");
        EnvelopeResponse loginResponse = tapoClient.callEncrypted(loginRequest, cookieStore);
        System.out.println(loginResponse.getResult().getResponse());

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
