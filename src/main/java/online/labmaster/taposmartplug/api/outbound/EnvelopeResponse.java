package online.labmaster.taposmartplug.api.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EnvelopeResponse {
    @JsonProperty("error_code")
    private int errorCode;
    private Result result;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
