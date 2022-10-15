package online.labmaster.taposmartplug.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.TapoException;
import online.labmaster.taposmartplug.api.inbound.TapoResponse;
import online.labmaster.taposmartplug.api.outbound.EnvelopeRequest;
import online.labmaster.taposmartplug.api.outbound.HandshakeRequest;
import online.labmaster.taposmartplug.api.inbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.inbound.HandshakeResponse;
import online.labmaster.taposmartplug.service.EncryptionService;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;


@Service
public class TapoClient {


    public static final String BASE_ERROR_MESSAGE = "Request error with error code: ";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EncryptionService encryptionService;

    public HandshakeResponse callHandshake(String plugIP, String publicKey, CookieStore cookieStore) throws IOException {
        Objects.requireNonNull(publicKey);
        Objects.requireNonNull(cookieStore);
        HttpResponse response = call(buildHandshakeRequest(plugIP, publicKey), cookieStore);
        HandshakeResponse handshakeResponse = objectMapper.readValue(response.getEntity().getContent(), HandshakeResponse.class);
        if (handshakeResponse == null || handshakeResponse.errorCode != 0) {
            throw new TapoException(BASE_ERROR_MESSAGE + (handshakeResponse != null ? String.valueOf(handshakeResponse.errorCode) : "no response"));
        }
        return handshakeResponse;
    }

    public <T extends TapoResponse> T callEncrypted(String plugIP, String encryptedMessage, CookieStore cookieStore, String token, Class<T> responseType, byte[] keys) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Objects.requireNonNull(encryptedMessage);
        Objects.requireNonNull(cookieStore);
        HttpResponse response = call(request(plugIP, encryptedMessage, token), cookieStore);
        EnvelopeResponse envelopeResponse = objectMapper.readValue(response.getEntity().getContent(), EnvelopeResponse.class);
        if (envelopeResponse == null || envelopeResponse.errorCode != 0) {
            throw new TapoException(BASE_ERROR_MESSAGE + (envelopeResponse != null ? String.valueOf(envelopeResponse.errorCode) : "no response"));
        }
        return objectMapper.readValue(encryptionService.decryptMessage(keys, envelopeResponse.result.response), responseType);
    }

    public CookieStore getCookieStore() {
        return new BasicCookieStore();
    }

    private HttpResponse call(HttpRequestBase request, CookieStore cookieStore) throws IOException {
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return httpClient.execute(request);
    }

    private HttpRequestBase buildHandshakeRequest(String plugIP, String publicKey) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost request = new HttpPost(getPlugUri(plugIP));
        HandshakeRequest handshake = new HandshakeRequest(publicKey);
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(handshake)));
        return request;
    }

    private HttpRequestBase request(String plugIP, String encodedRequest, String token) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost request = new HttpPost(getPlugUri(plugIP) + (token != null ? "?token=" + token : ""));
        EnvelopeRequest handshake = new EnvelopeRequest(encodedRequest);
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(handshake)));
        return request;
    }

    private String getPlugUri(String plugIP) {
        return "http://" + plugIP + "/app";
    }

}
