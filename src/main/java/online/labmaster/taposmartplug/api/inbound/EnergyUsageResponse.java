package online.labmaster.taposmartplug.api.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class EnergyUsageResponse extends TapoResponse {

    public EnergyUsage result;

    public class EnergyUsage {
        @JsonProperty("today_runtime")
        public Integer todayRuntime;

        @JsonProperty("month_runtime")
        public Integer monthRuntime;

        @JsonProperty("today_energy")
        public Integer todayEnergy;

        @JsonProperty("month_energy")
        public Integer monthEnergy;

        @JsonProperty("past24h")
        public List<Integer> past24h;

        @JsonProperty("past30d")
        public List<Integer> past30d;

        @JsonProperty("past1y")
        public List<Integer> past1y;

        @JsonProperty("past7d")
        public Integer[][] past7d;

        @JsonProperty("current_power")
        public Integer currentPower;

        @JsonProperty("local_time")
        public String localTime;
    }
}
