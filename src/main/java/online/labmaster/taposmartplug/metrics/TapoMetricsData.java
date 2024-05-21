package online.labmaster.taposmartplug.metrics;

import online.labmaster.taposmartplug.exception.TapoException;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;

public record TapoMetricsData(EnergyUsageResponse energyUsageResponse, DeviceInfoResponse deviceInfoResponse) {

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
