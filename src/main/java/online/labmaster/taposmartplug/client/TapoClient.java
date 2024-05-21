package online.labmaster.taposmartplug.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import online.labmaster.taposmartplug.exception.TapoAuthException;
import online.labmaster.taposmartplug.exception.TapoException;
import online.labmaster.taposmartplug.api.inbound.TapoResponse;
import online.labmaster.taposmartplug.api.outbound.EnvelopeRequest;
import online.labmaster.taposmartplug.api.outbound.HandshakeRequest;
import online.labmaster.taposmartplug.api.inbound.EnvelopeResponse;
import online.labmaster.taposmartplug.api.inbound.HandshakeResponse;
import online.labmaster.taposmartplug.api.outbound.TapoRequest;
import online.labmaster.taposmartplug.service.EncryptionService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.concat;
import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.sha256;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;

@Service
@Slf4j
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
            throw new TapoException(BASE_ERROR_MESSAGE + (handshakeResponse != null ? String.valueOf(handshakeResponse.errorCode) : "no response"), handshakeResponse.errorCode);
        }
        return handshakeResponse;
    }

    public <T extends TapoResponse> T callEncrypted(String plugIP, String encryptedMessage, CookieStore cookieStore, String token, Class<T> responseType, byte[] keys) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Objects.requireNonNull(encryptedMessage);
        Objects.requireNonNull(cookieStore);
        HttpResponse response = call(request(plugIP, encryptedMessage, token), cookieStore);
        EnvelopeResponse envelopeResponse = objectMapper.readValue(response.getEntity().getContent(), EnvelopeResponse.class);
        if (envelopeResponse == null || envelopeResponse.errorCode != 0) {
            throw new TapoException(BASE_ERROR_MESSAGE + (envelopeResponse != null ? String.valueOf(envelopeResponse.errorCode) : "no response"), envelopeResponse.errorCode);
        }
        return objectMapper.readValue(encryptionService.decryptMessage(keys, envelopeResponse.result.response), responseType);
    }

    public byte[] callKlapHandshake1(final String url, final byte[] localSeed, final byte[] authHash, CookieStore cookieStore) throws IOException {
        log.info("Performing KLAP handshake 1 for: {}", url);

        HttpPost post = new HttpPost(url + "/handshake1");
        post.setEntity(new ByteArrayEntity(localSeed));

        HttpResponse httpResponse = call(post, cookieStore);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != 200) {
            log.error("Handshake1 response error: " + statusCode);
            throw new TapoAuthException("Error performing KLAP handshake 1. Response status: " + statusCode);
        }

        byte[] responseBody = httpResponse.getEntity().getContent().readAllBytes();
        byte[] remoteSeed = Arrays.copyOfRange(responseBody, 0, 16);
        byte[] serverHash = Arrays.copyOfRange(responseBody, 16, responseBody.length);
        byte[] localHash = sha256(concat(localSeed, remoteSeed, authHash));

        if (!Arrays.equals(localHash, serverHash)) {
            log.error("Local hash does not match server hash");
            throw new TapoAuthException("Error performing KLAP handshake 1.");
        }

        log.info("KLAP Handshake 1 successful.");
        return remoteSeed;
    }

    public void callKlapHandshake2(String url, byte[] localSeed, byte[] remoteSeed, byte[] authHash, CookieStore cookieStore) throws IOException {
        log.info("Performing KLAP handshake 2 for: {}", url);

        byte[] payload = DigestUtils.sha256(concat(remoteSeed, localSeed, authHash));

        HttpPost request = new HttpPost(url + "/handshake2");
        request.setHeader(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE);
        request.setEntity(new ByteArrayEntity(payload));

        HttpResponse response = call(request, cookieStore);

        if (response.getStatusLine().getStatusCode() != 200) {
            log.error("Handshake 2 error: " + response.getStatusLine().getStatusCode());
            throw new TapoAuthException("Error performing KLAP handshake 2.");
        }

        log.info("KLAP Handshake 2 successful.");
    }

    public <T extends TapoResponse> T request(String plugIP, TapoRequest tapoRequest, Map<String, Object> params, TapoKeys tapoKeys, Class<T> responseType) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        // Construct payload
        Map<String, Object> payload = getPayload(tapoRequest, params);

        // Encrypt request payload as json
        String payloadJson = objectMapper.writeValueAsString(payload);
        byte[] encrypted = encryptionService.encrypt(tapoKeys, payloadJson.getBytes(StandardCharsets.UTF_8));

        // Execute call
        byte[] result = executeRequest(plugIP,"request", encrypted, Map.of("seq", String.valueOf(tapoKeys.getKlapSeq())), tapoKeys.getCookieStore());

        // Decrypt response
        String decrypted = new String(encryptionService.decrypt(tapoKeys, result), StandardCharsets.UTF_8);
        log.info("Response: {}", decrypted);

        Map<String, Object> data = objectMapper.readValue(decrypted, Map.class);

        // Process envelope, get result or error code
        int errorCode = (int) data.get("error_code");
        if (errorCode != 0) {
            log.error("Error: " + data);
            tapoKeys.setKlapKey(null); // reset key?
            throw new TapoException("Error code from TP-Link TAPO: " + errorCode);
        }

        return objectMapper.readValue(decrypted, responseType);
    }

    public CookieStore getCookieStore() {
        return new BasicCookieStore();
    }

    private byte[] executeRequest(String plugIP, String path, byte[] data, Map<String, String> params, CookieStore cookieStore) throws IOException {
        HttpPost request = new HttpPost(buildUrl(plugIP, path, params));
        request.setHeader(CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE);
        request.setEntity(new ByteArrayEntity(data));

        HttpResponse response = call(request, cookieStore);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
            log.error("Forbidden received from Tapo device from {}", plugIP);
            throw new TapoAuthException("Auth error");
        }

        if (response.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP request failed with response code " + response.getStatusLine().getStatusCode());
        }

        return response.getEntity().getContent().readAllBytes();
    }

    private static Map<String, Object> getPayload(TapoRequest tapoRequest, Map<String, Object> params) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("method", tapoRequest.getMethod());
        if (params != null) {
            payload.put("params", params);
        }
        return payload;
    }

    private static String buildUrl(String plugIP, String path, Map<String, String> params) {
        // Construct URL with path and params
        StringBuilder urlBuilder = new StringBuilder("http://");
        urlBuilder.append(plugIP).append("/app/").append(path);

        if (params != null && !params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            // Remove the trailing '&'
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        return String.valueOf(urlBuilder);
    }

    private HttpResponse call(HttpRequestBase request, CookieStore cookieStore) throws IOException {
        log.debug("Cookies: {}", cookieStore.getCookies().toString());
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
