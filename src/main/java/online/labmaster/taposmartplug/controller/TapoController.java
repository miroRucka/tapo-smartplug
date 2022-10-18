package online.labmaster.taposmartplug.controller;

import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.api.inbound.DeviceUsageResponse;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;
import online.labmaster.taposmartplug.service.TapoKeysService;
import online.labmaster.taposmartplug.service.TapoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TapoController {

    @Autowired
    private TapoService tapoService;

    @Autowired
    private TapoKeysService tapoKeysService;

    @Value("${tapo.plug.IPs}")
    private List<String> plugIPs;

    @RequestMapping(path = "/load-keys", method = RequestMethod.GET)
    public ResponseEntity test(@RequestParam String plugIP) {
        checkPlugIP(plugIP);
        tapoKeysService.getTapoKeys(plugIP);
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @RequestMapping(path = "/energy-usage", method = RequestMethod.GET)
    public EnergyUsageResponse energyUsed(@RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        checkPlugIP(plugIP);
        return tapoService.energyUsed(plugIP);
    }

    @RequestMapping(path = "/device-info", method = RequestMethod.GET)
    public DeviceInfoResponse deviceInfo(@RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        checkPlugIP(plugIP);
        return tapoService.deviceInfo(plugIP);
    }

    @RequestMapping(path = "/device-usage", method = RequestMethod.GET)
    public DeviceUsageResponse deviceUsage(@RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        checkPlugIP(plugIP);
        return tapoService.deviceUsage(plugIP);
    }

    private void checkPlugIP(String plugIP) {
        if (!plugIPs.contains(plugIP))
            throw new IllegalArgumentException("Plug IP: " + plugIP + " is illegal, please use ip from property tapo.plug.IPs " + plugIPs);
    }
}
