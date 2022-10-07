package online.labmaster.taposmartplug.api.inbound;

public class HandshakeRequest {

    private final String method;
    private final KeyParam params;

    public HandshakeRequest(String method, KeyParam keyParam) {
        this.method = method;
        this.params = keyParam;
    }

    public HandshakeRequest(String method, String key) {
        this.method = method;
        this.params = new KeyParam(key);
    }

    public String getMethod() {
        return method;
    }

    public KeyParam getParams() {
        return params;
    }
}
