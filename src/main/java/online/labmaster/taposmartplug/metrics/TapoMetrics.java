package online.labmaster.taposmartplug.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.extern.slf4j.Slf4j;
import online.labmaster.taposmartplug.exception.TapoAuthException;
import online.labmaster.taposmartplug.exception.TapoException;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.service.TapoKeysService;
import online.labmaster.taposmartplug.service.TapoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class TapoMetrics {

    public static final String TAPO_ENERGY_USAGE_CURRENT_POWER_IN_WATT = "tapo.energyUsage.currentPowerInWatt";
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
    public static final int TAPO_ERR_CODE = 9999;

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private TapoService tapoService;

    @Autowired
    private TapoKeysService tapoKeysService;

    private Map<String, TapoMetricsData> tapoMetricsData = new HashMap<>();

    @Value("${tapo.plug.IPs}")
    private List<String> plugIPs;

    @Async
    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public void registerTapoMetrics() {
        log.info("-> start measure ->");
        for (String plugIP : plugIPs) {
            try {
                registerMetricsByIP(plugIP);
            } catch (IOException e) {
                log.error("Tapo Device is unavailable at {}", plugIP);
            }
        }
    }

    private void registerMetricsByIP(String plugIP) throws IOException {
        try {
            tapoMetricsData.put(plugIP, new TapoMetricsData(tapoService.energyUsed(plugIP), tapoService.deviceInfo(plugIP), tapoService.currentPower(plugIP)));
            Gauge.builder(TAPO_ENERGY_USAGE_CURRENT_POWER_IN_WATT, () -> tapoMetricsData.get(plugIP).getCurrentPowerInfo().currentPower).strongReference(true).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_CURRENT_POWER, () -> tapoMetricsData.get(plugIP).getEnergyUsage().currentPower).strongReference(true).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_ENERGY, () -> tapoMetricsData.get(plugIP).getEnergyUsage().todayEnergy).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_ENERGY, () -> tapoMetricsData.get(plugIP).getEnergyUsage().monthEnergy).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_TODAY_RUNTIME, () -> tapoMetricsData.get(plugIP).getEnergyUsage().todayRuntime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_ENERGY_USAGE_MONTH_RUNTIME, () -> tapoMetricsData.get(plugIP).getEnergyUsage().monthRuntime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_ON_TIME, () -> tapoMetricsData.get(plugIP).getDeviceInfo().onTime).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_RSSI, () -> Double.parseDouble(tapoMetricsData.get(plugIP).getDeviceInfo().rssi)).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
            Gauge.builder(TAPO_DEVICE_INFO_DEVICE_ON, () -> tapoMetricsData.get(plugIP).getDeviceInfo().deviceOn ? 1 : 0).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
        } catch (TapoException e) {
            handleTapoException(plugIP, e);
        } catch (TapoAuthException e) {
            log.error("Request unauthorized. Trying to reload keys for: {}", plugIP);
            tapoKeysService.invalidateAndReloadKeys(plugIP);
        } catch (Exception e) {
            log.error("Cannot retrieve metrics for Tapo plug at ip: {}, {}", plugIP, e.getMessage());
            tapoKeysService.invalidateAndReloadKeys(plugIP);
        }
    }

    private void handleTapoException(String plugIP, TapoException e) {
        if (TAPO_ERR_CODE == e.getErrorCode()) {
            Gauge.builder(TAPO_DEVICE_INFO_DEVICE_ON, () -> 0).tags(buildPlugTags(tapoMetricsData.get(plugIP).getDeviceInfo())).register(registry);
        } else {
            log.error("Cannot retrieve metrics for Tapo plug at ip: {}", plugIP, e);
        }
    }

    private Iterable<Tag> buildPlugTags(DeviceInfoResponse.DeviceInfo deviceInfo) {
        Objects.requireNonNull(deviceInfo);
        List<Tag> tags = new ArrayList<>();
        tags.add(new ImmutableTag(DEVICE_ID, deviceInfo.deviceId));
        tags.add(new ImmutableTag(IP, deviceInfo.ip));
        tags.add(new ImmutableTag(NICKNAME, base64Decode(deviceInfo.nickname)));
        return tags;
    }

    private String base64Decode(String message) {
        return new String(Base64.getDecoder().decode(message));
    }

}
