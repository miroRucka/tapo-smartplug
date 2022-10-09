package online.labmaster.taposmartplug;

import online.labmaster.taposmartplug.service.TapoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class TapoSmartplugApplication {

	public static void main(String[] args) {
		SpringApplication.run(TapoSmartplugApplication.class, args);
	}

}
