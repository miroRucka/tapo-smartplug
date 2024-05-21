package online.labmaster.taposmartplug.client;

import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.CookieStore;

import java.util.Arrays;

@Getter
@Setter
public class TapoKeys {

    private String token;
    private byte[] keys;
    private CookieStore cookieStore;

    private boolean useKlapProtocol;
    byte[] klapKey;
    byte[] klapIvSeq;
    byte[] klapIv;
    int klapSeq;
    byte[] klapSig;

    public TapoKeys(String token, byte[] keys, CookieStore cookieStore) {
        this.token = token;
        this.keys = keys;
        this.cookieStore = cookieStore;
    }

    public TapoKeys(byte[] klapKey, byte[] klapIvSeq, byte[] klapIv, int klapSeq, byte[] klapSig, CookieStore cookieStore) {
        this.klapKey = klapKey;
        this.klapIvSeq = klapIvSeq;
        this.klapIv = klapIv;
        this.klapSeq = klapSeq;
        this.klapSig = klapSig;
        this.cookieStore = cookieStore;
        this.useKlapProtocol = true;
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
