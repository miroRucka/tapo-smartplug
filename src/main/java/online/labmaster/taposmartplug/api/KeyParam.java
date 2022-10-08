package online.labmaster.taposmartplug.api;

public class KeyParam {

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public KeyParam withKey(String key) {
        this.key = key;
        return this;
    }

    public String getKey() {
        return key;
    }
}
