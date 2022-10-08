package online.labmaster.taposmartplug.api.inbound;

import online.labmaster.taposmartplug.api.RequestParam;

public class EnvelopeRequest {

    public static final String SECURE_PASSTHROUGH_METHOD = "securePassthrough";

    private String method;
    private RequestParam params;

    public EnvelopeRequest(String method, RequestParam payload) {
        this.method = method;
        this.params = payload;
    }

    public EnvelopeRequest(String method, String payload) {
        this.method = method;
        this.params = new RequestParam(payload);
    }

    public String getMethod() {
        return method;
    }

    public RequestParam getParams() {
        return params;
    }
}
