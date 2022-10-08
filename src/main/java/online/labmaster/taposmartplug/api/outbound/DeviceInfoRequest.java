package online.labmaster.taposmartplug.api.outbound;

public class DeviceInfoRequest extends TapoRequest {

    private static final String GET_DEVICE_INFO = "get_device_info";

    public DeviceInfoRequest(String terminalUUID) {
        super(GET_DEVICE_INFO);
        setTerminalUUID(terminalUUID);
    }
}
