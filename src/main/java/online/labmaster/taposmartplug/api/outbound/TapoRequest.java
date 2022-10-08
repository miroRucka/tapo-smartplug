package online.labmaster.taposmartplug.api.outbound;

public abstract class TapoRequest {

    private final String method;

    public TapoRequest(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
