package online.labmaster.taposmartplug.exception;

public class TapoException extends RuntimeException {

    private int errorCode;

    public TapoException(String message) {
        super(message);
    }

    public TapoException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
