package online.labmaster.taposmartplug.api.outbound;

public class DeviceUsageRequest extends TapoRequest{
    private static final String GET_DEVICE_USAGE = "get_device_usage";

    public DeviceUsageRequest(String terminalUUID) {
        super(GET_DEVICE_USAGE);
        setTerminalUUID(terminalUUID);
    }
}
