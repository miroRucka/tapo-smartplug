package online.labmaster.taposmartplug.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import online.labmaster.taposmartplug.service.TapoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TapoMetrics {

    public static final Logger logger = LoggerFactory.getLogger(TapoMetrics.class);

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
            Gauge.builder("tapo.currentPower", tapoMetricsData, energyUsage -> tapoMetricsData.getEnergyUsage().currentPower)
                    .tag("device_id", tapoMetricsData.getDeviceInfo().device_id)
                    .tag("rssi", tapoMetricsData.getDeviceInfo().rssi)
                    .tag("device_on", String.valueOf(tapoMetricsData.getDeviceInfo().device_on))
                    .tag("overheated", String.valueOf(tapoMetricsData.getDeviceInfo().overheated))
                    .register(registry);
        } catch (Exception e) {
            logger.error("cannot retrieve tapo metrics", e);
        }


    }
}
