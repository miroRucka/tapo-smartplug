package online.labmaster.taposmartplug.api.outbound;

public class CurrentPowerRequest extends TapoRequest {

    private static final String GET_CURRENT_POWER_METHOD = "get_current_power";

    public CurrentPowerRequest() {
      super(GET_CURRENT_POWER_METHOD);
    }

    public CurrentPowerRequest(String terminalUUID) {
        super(GET_CURRENT_POWER_METHOD);
        setTerminalUUID(terminalUUID);
    }
}
