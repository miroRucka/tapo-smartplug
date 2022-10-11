package online.labmaster.taposmartplug.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.service.TapoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class TapoMetrics {

    public static final Logger logger = LoggerFactory.getLogger(TapoMetrics.class);
    public static final String TAPO_ENERGY_USAGE_CURRENT_POWER = "tapo.energyUsage.currentPower";
    public static final String TAPO_ENERGY_USAGE_TODAY_ENERGY = "tapo.energyUsage.todayEnergy";
    public static final String TAPO_ENERGY_USAGE_MONTH_ENERGY = "tapo.energyUsage.monthEnergy";
    public static final String TAPO_ENERGY_USAGE_TODAY_RUNTIME = "tapo.energyUsage.todayRuntime";
    public static final String TAPO_ENERGY_USAGE_MONTH_RUNTIME = "tapo.energyUsage.monthRuntime";
    public static final String TAPO_DEVICE_INFO_ON_TIME = "tapo.deviceInfo.on_time";
    public static final String TAPO_DEVICE_INFO_RSSI = "tapo.deviceInfo.rssi";
    public static final String TAPO_DEVICE_INFO_DEVICE_ON = "tapo.deviceInfo.device_on";
    public static final String DEVICE_ID = "device_id";
    public static final String IP = "ip";
    public static final String NICKNAME = "nickname";

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private TapoService tapoService;

    @Autowired
    private TapoMetricsData tapoMetricsData;

    @Async
    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void registerTapoMetrics() {
        try {
            tapoMetricsData.setEnergyUsageResponse(tapoService.energyUsed());
            tapoMetricsData.setDeviceInfoResponse(tapoService.deviceInfo());
            Gauge.builder(TAPO_ENERGY_USAGE_CURRENT_POWER, tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().currentPower).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_ENERGY, tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().todayEnergy).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_ENERGY, tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().monthEnergy).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_RUNTIME, tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().todayRuntime).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_RUNTIME, tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().monthRuntime).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_ON_TIME, tapoMetricsData, energyUsage -> tapoMetricsData.getDeviceInfo().onTime).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_RSSI, tapoMetricsData, energyUsage -> Double.parseDouble(tapoMetricsData.getDeviceInfo().rssi)).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_DEVICE_ON, tapoMetricsData, energyUsage -> tapoMetricsData.getDeviceInfo().deviceOn ? 1 : 0).tags(buildPlugTags(tapoMetricsData.getDeviceInfo())).register(registry);
        } catch (Exception e) {
            logger.error("cannot retrieve tapo metrics", e);
        }
    }

    private Iterable<Tag> buildPlugTags(DeviceInfoResponse.DeviceInfo deviceInfo) {
        Objects.requireNonNull(deviceInfo);
        List<Tag> tags = new ArrayList<>();
        tags.add(new ImmutableTag(DEVICE_ID, deviceInfo.deviceId));
        tags.add(new ImmutableTag(IP, deviceInfo.ip));
        tags.add(new ImmutableTag(NICKNAME, deviceInfo.nickname));
        return tags;
    }
}
