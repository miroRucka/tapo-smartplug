package online.labmaster.taposmartplug.api.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NicknameRequest extends TapoRequest {

    private static final String SET_DEVICE_INFO = "set_device_info";

    private final NicknameParam params;

    public NicknameRequest(String terminalUUID, NicknameParam params) {
        super(SET_DEVICE_INFO);
        setTerminalUUID(terminalUUID);
        this.params = params;
    }

    public NicknameParam getParams() {
        return params;
    }

    public static class NicknameParam {

        @JsonProperty("nickname")
        public String nickname;

        @JsonProperty("device_id")
        public String deviceId;

    }
}
