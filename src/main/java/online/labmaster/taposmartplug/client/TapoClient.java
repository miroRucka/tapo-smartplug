package online.labmaster.taposmartplug.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.EnvelopeRequest;
import online.labmaster.taposmartplug.api.inbound.HandshakeRequest;
import online.labmaster.taposmartplug.api.outbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.outbound.HandshakeResponse;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Service
public class TapoClient {


    @Autowired
    private ObjectMapper objectMapper;

    @Value("${tapo.plug.uri:http://192.168.241.206/app}")
    private String plugUri;

    public HandshakeResponse callHandshake(String publicKey, CookieStore cookieStore) throws IOException {
        HttpResponse response = call(buildHandshakeRequest(publicKey), cookieStore);
        return objectMapper.readValue(response.getEntity().getContent(), HandshakeResponse.class);
    }

    public EnvelopeResponse callEncrypted(String encryptedMessage, CookieStore cookieStore) throws IOException {
        HttpResponse response = call(request(encryptedMessage), cookieStore);
        return objectMapper.readValue(response.getEntity().getContent(), EnvelopeResponse.class);
    }

    public CookieStore getCookieStore() {
        return new BasicCookieStore();
    }

    private HttpResponse call(HttpRequestBase request, CookieStore cookieStore) throws IOException {
        HttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        return httpClient.execute(request);
    }

    private HttpRequestBase buildHandshakeRequest(String publicKey) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost request = new HttpPost(plugUri);
        HandshakeRequest handshake = new HandshakeRequest(HandshakeRequest.HANDSHAKE_METHOD, publicKey);
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(handshake)));
        return request;
    }

    private HttpRequestBase request(String encodedRequest) throws UnsupportedEncodingException, JsonProcessingException {
        HttpPost request = new HttpPost(plugUri);
        EnvelopeRequest handshake = new EnvelopeRequest(EnvelopeRequest.SECURE_PASSTHROUGH_METHOD, encodedRequest);
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(handshake)));
        return request;
    }

}
