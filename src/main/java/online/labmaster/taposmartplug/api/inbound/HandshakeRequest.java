package online.labmaster.taposmartplug.api.inbound;

import online.labmaster.taposmartplug.api.KeyParam;

public class HandshakeRequest {

    public static final String HANDSHAKE_METHOD = "handshake";

    private final String method;
    private final KeyParam params;

    public HandshakeRequest(String method, KeyParam keyParam) {
        this.method = method;
        this.params = keyParam;
    }

    public HandshakeRequest(String method, String key) {
        this.method = method;
        this.params = new KeyParam().withKey(key);
    }

    public String getMethod() {
        return method;
    }

    public KeyParam getParams() {
        return params;
    }
}
