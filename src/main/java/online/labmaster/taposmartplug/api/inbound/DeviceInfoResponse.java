package online.labmaster.taposmartplug.api.inbound;

public class DeviceInfoResponse extends TapoResponse {

    public DeviceInfo result;

    public static class DeviceInfo {
        public String device_id;
        public String fw_ver;
        public String hw_ver;
        public String type;
        public String model;
        public String mac;
        public String hw_id;
        public String fw_id;
        public String oem_id;
        public String ip;
        public int time_diff;
        public String ssid;
        public String rssi;
        public String signal_level;
        public String latitude;
        public String longitude;
        public String lang;
        public String avatar;
        public String region;
        public String specs;
        public String nickname;
        public boolean has_set_location_info;
        public boolean device_on;
        public int on_time;
        public boolean overheated;
        public DefaultStates default_states;

        public class DefaultStates {
            public String type;
            public Object state;
        }
    }
}
