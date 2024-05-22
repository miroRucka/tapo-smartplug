package online.labmaster.taposmartplug.api.outbound;

public class EnergyUsageRequest extends TapoRequest{

    private static final String GET_ENERGY_USAGE_METHOD = "get_energy_usage";

    public EnergyUsageRequest() {
        super(GET_ENERGY_USAGE_METHOD);
    }

    public EnergyUsageRequest(String terminalUUID) {
        super(GET_ENERGY_USAGE_METHOD);
        setTerminalUUID(terminalUUID);
    }
}
