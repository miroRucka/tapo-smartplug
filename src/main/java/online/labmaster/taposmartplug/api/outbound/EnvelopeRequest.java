package online.labmaster.taposmartplug.api.outbound;

import online.labmaster.taposmartplug.api.RequestParam;

public class EnvelopeRequest extends TapoRequest {

    private static final String SECURE_PASSTHROUGH_METHOD = "securePassthrough";
    private RequestParam params;

    public EnvelopeRequest(RequestParam payload) {
        super(SECURE_PASSTHROUGH_METHOD);
        this.params = payload;
    }

    public EnvelopeRequest(String payload) {
        super(SECURE_PASSTHROUGH_METHOD);
        this.params = new RequestParam(payload);
    }

    public RequestParam getParams() {
        return params;
    }
}
