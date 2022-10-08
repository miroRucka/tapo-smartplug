package online.labmaster.taposmartplug.api.outbound;

public class EnergyUsageRequest extends TapoRequest{

    private static final String GET_ENERGY_USAGE_METHOD = "get_energy_usage";
    private final String terminalUUID;

    public EnergyUsageRequest(String terminalUUID) {
        super(GET_ENERGY_USAGE_METHOD);
        this.terminalUUID = terminalUUID;
    }

    public String getTerminalUUID() {
        return terminalUUID;
    }
}
