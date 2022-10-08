package online.labmaster.taposmartplug.client;

import org.apache.http.client.CookieStore;

import java.util.Arrays;

public class TapoKeys {

    private String token;
    private byte[] keys;
    private CookieStore cookieStore;

    public TapoKeys(String token, byte[] keys, CookieStore cookieStore) {
        this.token = token;
        this.keys = keys;
        this.cookieStore = cookieStore;
    }

    public String getToken() {
        return token;
    }

    public byte[] getKeys() {
        return keys;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    @Override
    public String toString() {
        return "TapoKeys{" +
                "token='" + token + '\'' +
                ", keys=" + Arrays.toString(keys) +
                ", cookieStore=" + cookieStore +
                '}';
    }
}
