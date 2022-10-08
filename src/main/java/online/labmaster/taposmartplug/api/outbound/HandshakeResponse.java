package online.labmaster.taposmartplug.api.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import online.labmaster.taposmartplug.api.KeyParam;

public class HandshakeResponse {
    @JsonProperty("error_code")
    private int errorCode;
    private KeyParam result;

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void setResult(KeyParam result) {
        this.result = result;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public KeyParam getResult() {
        return result;
    }
}
