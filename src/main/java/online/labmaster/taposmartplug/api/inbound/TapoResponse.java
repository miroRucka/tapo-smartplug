package online.labmaster.taposmartplug.api.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapoResponse {
    @JsonProperty("error_code")
    public int errorCode;
}
