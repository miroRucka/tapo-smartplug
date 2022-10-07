package online.labmaster.taposmartplug.encryption;

import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class EncryptionService {

    public KeyPair generateKeyPair(){
        return null;
    }

    public String transformPublicCertificate(PublicKey publicKey){
        return null;
    }

    public byte[] decryptKeys(PrivateKey privateKey, String key){
        return null;
    }

    public String decryptMessage(byte[] keys, String message){
        return null;
    }

    public String encryptMessage(byte[] keys, String message){
        return null;
    }

    private byte[] getKey(byte[] keys){
        return null;
    }

    private byte[] getIV(byte[] keys){
        return null;
    }
}
