package online.labmaster.taposmartplug.metrics;

import online.labmaster.taposmartplug.api.TapoException;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;
import org.springframework.stereotype.Component;

@Component
public class TapoMetricsData {

    private EnergyUsageResponse energyUsageResponse;

    private DeviceInfoResponse deviceInfoResponse;

    public EnergyUsageResponse.EnergyUsage getEnergyUsage() {
        if (energyUsageResponse != null) {
            return energyUsageResponse.result;
        }
        throw new TapoException("cannot get energy usage, response is null");
    }

    public void setEnergyUsageResponse(EnergyUsageResponse energyUsageResponse) {
        this.energyUsageResponse = energyUsageResponse;
    }


    public DeviceInfoResponse.DeviceInfo getDeviceInfo() {
        if (deviceInfoResponse != null) {
            return deviceInfoResponse.result;
        }
        throw new TapoException("cannot get device info, response is null");
    }

    public void setDeviceInfoResponse(DeviceInfoResponse deviceInfoResponse) {
        this.deviceInfoResponse = deviceInfoResponse;
    }
}
