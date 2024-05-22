package online.labmaster.taposmartplug.utils;

import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@UtilityClass
public class TapoKlapProtocolUtils {

  public static byte[] generateRandomBytes(int length) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[length];
    secureRandom.nextBytes(randomBytes);
    return randomBytes;
  }

  public static byte[] sha256(byte[] value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return digest.digest(value);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 algorithm not found", e);
    }
  }

  public static byte[] sha1(byte[] value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-1");
      return digest.digest(value);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-1 algorithm not found", e);
    }
  }

  public static byte[] calcAuthHash(String username, String password) {
    byte[] usernameSha1 = sha1(username.getBytes());
    byte[] passwordSha1 = sha1(password.getBytes());

    byte[] concatenatedHashes = concat(usernameSha1, passwordSha1);
    return sha256(concatenatedHashes);
  }

  public static byte[] concat(byte[]... arrays) {
    int totalLength = 0;
    for (byte[] array : arrays) {
      totalLength += array.length;
    }
    byte[] result = new byte[totalLength];
    int currentIndex = 0;
    for (byte[] array : arrays) {
      System.arraycopy(array, 0, result, currentIndex, array.length);
      currentIndex += array.length;
    }
    return result;
  }

  public static byte[] intToByteArray(int value) {
    return new byte[] {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
    };
  }

  public static int byteArrayToInt(byte[] bytes) {
    int value = 0;
    for (byte b : bytes) {
      value = (value << 8) | (b & 0xFF);
    }
    return value;
  }

}
