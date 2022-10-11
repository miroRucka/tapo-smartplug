package online.labmaster.taposmartplug.service;

import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Formatter;
import java.util.Objects;

@Service
public class EncryptionService {

    public static final String RSA_ECB_PKCS_1_PADDING = "RSA/ECB/PKCS1Padding";
    public static final String AES_CBC_PKCS_5_PADDING = "AES/CBC/PKCS5PADDING";
    public static final String RSA = "RSA";
    public static final String SHA_1 = "SHA-1";
    public static final String ALGORITHM = "AES";
    public static final int KEY_SIZE = 1024;

    private final Base64.Encoder encoder = Base64.getEncoder();
    private final Base64.Decoder decoder = Base64.getDecoder();

    public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA);
        generator.initialize(KEY_SIZE);
        return generator.generateKeyPair();
    }

    public String transformPublicCertificate(PublicKey publicKey) {
        StringBuilder result = new StringBuilder();
        result.append("-----BEGIN PUBLIC KEY-----\n");
        result.append(encoder.encodeToString(Objects.requireNonNull(publicKey).getEncoded()));
        result.append("\n-----END PUBLIC KEY-----");
        return String.valueOf(result);
    }

    public byte[] decryptKeys(PrivateKey privateKey, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(privateKey);
        byte[] decodedBytes = Base64.getDecoder().decode(Objects.requireNonNull(key));
        Cipher decryptCipher = Cipher.getInstance(RSA_ECB_PKCS_1_PADDING);
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        return decryptCipher.doFinal(decodedBytes);
    }

    public String encryptMessage(byte[] keys, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(keys);
        Objects.requireNonNull(message);
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(keys), ALGORITHM);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(getIV(keys));
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, paramSpec);
        return encoder.encodeToString(cipher.doFinal(message.getBytes(StandardCharsets.UTF_8)));
    }

    public String decryptMessage(byte[] keys, String message) throws InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS_5_PADDING);
        SecretKeySpec secretKeySpec = new SecretKeySpec(getKey(keys), ALGORITHM);
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(getIV(keys));
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, paramSpec);
        return new String(cipher.doFinal(decoder.decode(message)));
    }

    public String encryptLoginName(String message) throws NoSuchAlgorithmException {
        MessageDigest crypt = MessageDigest.getInstance(SHA_1);
        crypt.reset();
        crypt.update(message.getBytes(StandardCharsets.UTF_8));
        return base64Encode(byteToHex(crypt.digest()));
    }

    public String base64Encode(String message) {
        return encoder.encodeToString(message.getBytes(StandardCharsets.UTF_8));
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private byte[] getKey(byte[] keys) {
        return Arrays.copyOfRange(keys, 0, 16);
    }

    private byte[] getIV(byte[] keys) {
        return Arrays.copyOfRange(keys, 16, 32);
    }
}
