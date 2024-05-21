package online.labmaster.taposmartplug.service;

import online.labmaster.taposmartplug.client.TapoKeys;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Formatter;
import java.util.Objects;

import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.concat;
import static online.labmaster.taposmartplug.utils.TapoKlapProtocolUtils.intToByteArray;

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

    public byte[] encrypt(TapoKeys tapoKeys, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        tapoKeys.setKlapSeq(tapoKeys.getKlapSeq() + 1);
        byte[] seqBytes = intToByteArray(tapoKeys.getKlapSeq());

        // Add PKCS#7 padding
        int padLength = 16 - (data.length % 16);
        byte[] paddedData = new byte[data.length + padLength];
        System.arraycopy(data, 0, paddedData, 0, data.length);
        for (int i = data.length; i < paddedData.length; i++) {
            paddedData[i] = (byte) padLength;
        }

        // Encrypt data with key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(concat(tapoKeys.getKlapIv(), seqBytes));
        SecretKeySpec keySpec = new SecretKeySpec(tapoKeys.getKlapKey(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] ciphertext = cipher.doFinal(paddedData);

        // Signature
        byte[] sigData = concat(tapoKeys.getKlapSig(), seqBytes, ciphertext);
        byte[] signature = DigestUtils.sha256(sigData);

        return concat(signature, ciphertext);
    }

    public byte[] decrypt(TapoKeys tapoKeys, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] seqBytes = intToByteArray(tapoKeys.getKlapSeq());

        // Decrypt data with key
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(concat(tapoKeys.getKlapIv(), seqBytes));
        SecretKeySpec keySpec = new SecretKeySpec(tapoKeys.getKlapKey(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        return cipher.doFinal(Arrays.copyOfRange(data, 32, data.length));
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
