package online.labmaster.taposmartplug.api.outbound;

public abstract class TapoRequest {

    private final String method;
    private  String terminalUUID;

    public TapoRequest(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public String getTerminalUUID() {
        return terminalUUID;
    }

    public void setTerminalUUID(String terminalUUID) {
        this.terminalUUID = terminalUUID;
    }
}
