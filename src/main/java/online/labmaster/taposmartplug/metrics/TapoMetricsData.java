package online.labmaster.taposmartplug.metrics;

import online.labmaster.taposmartplug.api.TapoException;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;

public class TapoMetricsData {

    private final EnergyUsageResponse energyUsageResponse;

    private final DeviceInfoResponse deviceInfoResponse;

    public TapoMetricsData(EnergyUsageResponse energyUsageResponse, DeviceInfoResponse deviceInfoResponse) {
        this.energyUsageResponse = energyUsageResponse;
        this.deviceInfoResponse = deviceInfoResponse;
    }

    public EnergyUsageResponse.EnergyUsage getEnergyUsage() {
        if (energyUsageResponse != null) {
            return energyUsageResponse.result;
        }
        throw new TapoException("cannot get energy usage, response is null");
    }


    public DeviceInfoResponse.DeviceInfo getDeviceInfo() {
        if (deviceInfoResponse != null) {
            return deviceInfoResponse.result;
        }
        throw new TapoException("cannot get device info, response is null");
    }

}
