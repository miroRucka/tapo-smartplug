package online.labmaster.taposmartplug.api.outbound;

public class DeviceDiagnoseRequest extends TapoRequest {

    public static final String GET_DIAGNOSE_STATUS = "get_diagnose_status";

    public DeviceDiagnoseRequest(String terminalUUID) {
        super(GET_DIAGNOSE_STATUS);
        setTerminalUUID(terminalUUID);
    }
}
