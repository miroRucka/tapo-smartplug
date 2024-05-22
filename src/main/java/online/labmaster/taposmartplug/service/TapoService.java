package online.labmaster.taposmartplug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.*;
import online.labmaster.taposmartplug.api.outbound.*;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.client.TapoKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class TapoService {

    @Autowired
    private TapoClient tapoClient;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TapoKeysService tapoKeysService;

    @Value("${tapo.plug.terminal.id}")
    private String terminalId;

    public CurrentPowerResponse currentPower(String plugIP) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        if (keys.isUseKlapProtocol()) {
            return tapoClient.request(plugIP, new CurrentPowerRequest(), null, keys, CurrentPowerResponse.class);
        } else {
            String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new CurrentPowerRequest(terminalId)));
            return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), CurrentPowerResponse.class, keys.getKeys());
        }
    }

    public EnergyUsageResponse energyUsed(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        if (keys.isUseKlapProtocol()) {
            return tapoClient.request(plugIP, new EnergyUsageRequest(), null, keys, EnergyUsageResponse.class);
        } else {
            String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new EnergyUsageRequest(terminalId)));
            return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), EnergyUsageResponse.class, keys.getKeys());
        }
    }

    public DeviceInfoResponse deviceInfo(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        if (keys.isUseKlapProtocol()) {
            return tapoClient.request(plugIP, new DeviceInfoRequest(), null, keys, DeviceInfoResponse.class);
        } else {
            String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceInfoRequest(terminalId)));
            return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), DeviceInfoResponse.class, keys.getKeys());
        }
    }

    public DeviceInfoResponse deviceRunningInfo(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceRunningInfoRequest(terminalId)));
        return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), DeviceInfoResponse.class, keys.getKeys());
    }

    public DeviceUsageResponse deviceUsage(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        if (keys.isUseKlapProtocol()) {
            return tapoClient.request(plugIP, new DeviceUsageRequest(), null, keys, DeviceUsageResponse.class);
        } else {
            String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceUsageRequest(terminalId)));
            return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), DeviceUsageResponse.class, keys.getKeys());
        }
    }

    public TapoResponse deviceDiagnoseStatus(String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceDiagnoseRequest(terminalId)));
        return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), TapoResponse.class, keys.getKeys());
    }

    public TapoResponse switchPlug(String plugIP, boolean plugOn) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new PlugSwitchRequest(terminalId, plugOn)));
        return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), TapoResponse.class, keys.getKeys());
    }

    public TapoResponse setNickname(String plugIP, NicknameRequest.NicknameParam nickname) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys(plugIP);
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new NicknameRequest(terminalId, nickname)));
        return tapoClient.callEncrypted(plugIP, encryptedRequest, keys.getCookieStore(), keys.getToken(), TapoResponse.class, keys.getKeys());
    }
}
