package online.labmaster.taposmartplug.api.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PlugSwitchRequest extends TapoRequest {

    private static final String SET_DEVICE_INFO = "set_device_info";

    private final SwitchParam params;

    public PlugSwitchRequest(String terminalUUID, boolean plugOn) {
        super(SET_DEVICE_INFO);
        setTerminalUUID(terminalUUID);
        this.params = new SwitchParam(plugOn);
    }

    public static class SwitchParam {

        public SwitchParam(boolean deviceOn) {
            this.deviceOn = deviceOn;
        }

        @JsonProperty("device_on")
        public boolean deviceOn;

    }
}
