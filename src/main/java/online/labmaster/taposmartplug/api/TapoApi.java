package online.labmaster.taposmartplug.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import online.labmaster.taposmartplug.api.inbound.DeviceUsageResponse;
import online.labmaster.taposmartplug.api.inbound.EnergyUsageResponse;
import online.labmaster.taposmartplug.api.inbound.TapoResponse;
import online.labmaster.taposmartplug.api.outbound.NicknameRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Validated
@Tag(name = "tapo-samrt-plug")
public interface TapoApi {

    @RequestMapping(path = "/load-keys", method = RequestMethod.GET)
    @Operation(summary = "Testing the handshake procedure, which results in the exchange of a certificate between the application and the socket, a session cookie and a token for access")
    ResponseEntity loadKeys(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @org.springframework.web.bind.annotation.RequestParam String plugIP);

    @RequestMapping(path = "/energy-usage", method = RequestMethod.GET)
    @Operation(summary = "Operation to obtain data on electricity consumption. The answer will be various overviews of current, monthly, weekly, daily consumption")
    EnergyUsageResponse energyUsed(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @org.springframework.web.bind.annotation.RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/device-info", method = RequestMethod.GET)
    @Operation(summary = "Basic data about the device such as ID, name, firmware, but also more advanced such as wifi signal")
    DeviceInfoResponse deviceInfo(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @org.springframework.web.bind.annotation.RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/device-running-info", method = RequestMethod.GET)
    @Operation(summary = "Basic data about the device such as ID, name, firmware, but also more advanced such as wifi signal")
    DeviceInfoResponse deviceRunningInfo(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @org.springframework.web.bind.annotation.RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/device-usage", method = RequestMethod.GET)
    @Operation(summary = "Operation obtains data about the use of the device - how many hours of operation for different periods of time")
    DeviceUsageResponse deviceUsage(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/device-diagnose-status", method = RequestMethod.GET)
    @Operation(summary = "Get diagnose status code")
    TapoResponse deviceDiagnoseStatus(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/plug-on", method = RequestMethod.POST)
    @Operation(summary = "Operation turns on the socket")
    TapoResponse plugOn(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/plug-off", method = RequestMethod.POST)
    @Operation(summary = "Operation turns off the socket")
    TapoResponse plugOff(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @RequestParam String plugIP) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    @RequestMapping(path = "/nickname", method = RequestMethod.POST)
    @Operation(summary = "Operations for setting a specific name for a socket")
    TapoResponse setNickname(@Parameter(name = "plugIP", description = "Is the ip address of the destination socket on the local network ", in = ParameterIn.QUERY, required = true) @RequestParam String plugIP, @Parameter(name = "nickname",description = "Specific name for a socket.") @RequestBody NicknameRequest.NicknameParam nickname) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;
}