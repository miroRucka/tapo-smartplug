package online.labmaster.taposmartplug.api.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeviceInfoResponse extends TapoResponse {

    public DeviceInfo result;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeviceInfo {
        @JsonProperty("device_id")
        public String deviceId;

        @JsonProperty("fw_ver")
        public String fwVer;

        @JsonProperty("hw_ver")
        public String hwVer;

        @JsonProperty("type")
        public String type;

        @JsonProperty("model")
        public String model;

        @JsonProperty("mac")
        public String mac;

        @JsonProperty("hw_id")
        public String hwId;

        @JsonProperty("fw_id")
        public String fwId;

        @JsonProperty("oem_id")
        public String oemId;

        @JsonProperty("ip")
        public String ip;

        @JsonProperty("time_diff")
        public int timeDiff;

        @JsonProperty("ssid")
        public String ssid;

        @JsonProperty("rssi")
        public String rssi;

        @JsonProperty("signal_level")
        public String signalLevel;

        @JsonProperty("latitude")
        public String latitude;

        @JsonProperty("longitude")
        public String longitude;

        @JsonProperty("lang")
        public String lang;

        @JsonProperty("avatar")
        public String avatar;

        @JsonProperty("region")
        public String region;

        @JsonProperty("specs")
        public String specs;

        @JsonProperty("nickname")
        public String nickname;

        @JsonProperty("has_set_location_info")
        public boolean hasSetLocationInfo;

        @JsonProperty("device_on")
        public boolean deviceOn;

        @JsonProperty("on_time")
        public int onTime;

        @JsonProperty("overheated")
        public boolean overheated;

        @JsonProperty("overcurrent_status")
        public String overcurrentStatus;

        @JsonProperty("power_protection_status")
        public String powerProtectionStatus;

        @JsonProperty("default_states")
        public DefaultStates defaultStates;

        public static class DefaultStates {
            public String type;
            public Object state;
        }
    }
}
