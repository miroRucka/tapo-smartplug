package online.labmaster.taposmartplug.api.outbound;

import online.labmaster.taposmartplug.api.KeyParam;

public class HandshakeRequest extends TapoRequest {

    private static final String HANDSHAKE_METHOD = "handshake";
    private final KeyParam params;

    public HandshakeRequest(KeyParam keyParam) {
        super(HANDSHAKE_METHOD);
        this.params = keyParam;
    }

    public HandshakeRequest(String key) {
        super(HANDSHAKE_METHOD);
        this.params = new KeyParam().withKey(key);
    }


    public KeyParam getParams() {
        return params;
    }
}
