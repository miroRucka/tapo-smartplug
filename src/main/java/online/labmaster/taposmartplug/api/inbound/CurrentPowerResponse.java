package online.labmaster.taposmartplug.api.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentPowerResponse extends TapoResponse {

    public CurrentPowerInfo result;

    public static class CurrentPowerInfo {
        @JsonProperty("current_power")
        public int currentPower;
    }
}
