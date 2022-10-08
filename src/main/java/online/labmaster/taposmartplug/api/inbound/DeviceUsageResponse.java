package online.labmaster.taposmartplug.api.inbound;

public class DeviceUsageResponse extends TapoResponse {

    public UsageResult result;

    public static class UsageResult {

        public Usage power_usage;
        public Usage time_usage;
        public Usage saved_power;

        public class Usage {
            public int today;
            public int past7;
            public int past30;
        }
    }


}
