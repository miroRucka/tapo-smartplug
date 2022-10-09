package online.labmaster.taposmartplug.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import online.labmaster.taposmartplug.api.inbound.*;
import online.labmaster.taposmartplug.api.outbound.DeviceInfoRequest;
import online.labmaster.taposmartplug.api.outbound.DeviceUsageRequest;
import online.labmaster.taposmartplug.api.outbound.EnergyUsageRequest;
import online.labmaster.taposmartplug.api.outbound.LoginRequest;
import online.labmaster.taposmartplug.client.TapoClient;
import online.labmaster.taposmartplug.client.TapoKeys;
import org.apache.http.client.CookieStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
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

    public EnergyUsageResponse energyUsed() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys();
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new EnergyUsageRequest(terminalId)));
        return tapoClient.callEncrypted(encryptedRequest, keys.getCookieStore(), keys.getToken(), EnergyUsageResponse.class, keys.getKeys());
    }

    public DeviceInfoResponse deviceInfo() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys();
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceInfoRequest(terminalId)));
        return tapoClient.callEncrypted(encryptedRequest, keys.getCookieStore(), keys.getToken(), DeviceInfoResponse.class, keys.getKeys());
    }

    public DeviceUsageResponse deviceUsage() throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        TapoKeys keys = tapoKeysService.getTapoKeys();
        String encryptedRequest = encryptionService.encryptMessage(keys.getKeys(), objectMapper.writeValueAsString(new DeviceUsageRequest(terminalId)));
        return tapoClient.callEncrypted(encryptedRequest, keys.getCookieStore(), keys.getToken(), DeviceUsageResponse.class, keys.getKeys());
    }
}
