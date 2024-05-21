package online.labmaster.taposmartplug;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import online.labmaster.taposmartplug.api.inbound.CurrentPowerResponse;
import online.labmaster.taposmartplug.api.inbound.DeviceInfoResponse;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TapoResponseParseTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  private static File file;

  @Test
  @SneakyThrows
  void testEnvelopedDeviceInfoResponseParse() {
    loadTestFile("get_device_info_response.json");
    DeviceInfoResponse deviceInfoResponse = objectMapper.readValue(file, DeviceInfoResponse.class);
    assertNotNull(deviceInfoResponse);
    assertEquals(0, deviceInfoResponse.errorCode);
  }

  @Test
  @SneakyThrows
  void testEnvelopedCurrentPowerResponseParse() {
    loadTestFile("get_current_power_response.json");
    CurrentPowerResponse currentPowerResponse = objectMapper.readValue(file, CurrentPowerResponse.class);
    assertNotNull(currentPowerResponse);
    assertEquals(0, currentPowerResponse.errorCode);
    assertEquals(0, currentPowerResponse.result.currentPower);
  }

  private void loadTestFile(String fileName) {
    file =
      new File(
        this.getClass()
          .getClassLoader()
          .getResource(fileName)
          .getFile());
  }

}
