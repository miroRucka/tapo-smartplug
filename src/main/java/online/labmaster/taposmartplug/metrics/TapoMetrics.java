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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Supplier;

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

    private Map<String, TapoMetricsData> tapoMetricsData = new HashMap<>();


    @Value("${tapo.plug.IPs}")
    private List<String> plugIPs;

    @Async
    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void registerTapoMetrics() {
        logger.info("-> start measure ->");
        for (String plugIP : plugIPs) {
            registerMetricsByIP(plugIP);
        }
    }

    private void registerMetricsByIP(String plugIP) {
        try {
            tapoMetricsData.put(plugIP, new TapoMetricsData(tapoService.energyUsed(plugIP), tapoService.deviceInfo(plugIP)));
            Gauge.builder(TAPO_ENERGY_USAGE_CURRENT_POWER, () -> tapoMetricsData.get(plugIP).getEnergyUsage().currentPower).strongReference(true).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_ENERGY, () -> tapoMetricsData.get(plugIP).getEnergyUsage().todayEnergy).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_ENERGY, () -> tapoMetricsData.get(plugIP).getEnergyUsage().monthEnergy).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_RUNTIME, () -> tapoMetricsData.get(plugIP).getEnergyUsage().todayRuntime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_RUNTIME, () -> tapoMetricsData.get(plugIP).getEnergyUsage().monthRuntime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_ON_TIME, () -> tapoMetricsData.get(plugIP).getDeviceInfo().onTime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_RSSI, () -> Double.parseDouble(tapoMetricsData.get(plugIP).getDeviceInfo().rssi)).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_DEVICE_ON, () -> tapoMetricsData.get(plugIP).getDeviceInfo().deviceOn ? 1 : 0).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
        } catch (Exception e) {
            logger.error("cannot retrieve tapo metrics for plug ip: " + plugIP, e);
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
