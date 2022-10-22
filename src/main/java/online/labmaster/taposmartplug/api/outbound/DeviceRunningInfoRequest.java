package online.labmaster.taposmartplug.api.outbound;

public class DeviceRunningInfoRequest extends TapoRequest {

    public static final String GET_DEVICE_RUNNING_INFO = "get_device_running_info";

    public DeviceRunningInfoRequest(String terminalUUID) {
        super(GET_DEVICE_RUNNING_INFO);
        setTerminalUUID(terminalUUID);
    }
}
