package dev.kirin.example.api.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ActiveProfiles("test")
public class CipherHelperTest {

	@Autowired
	private CipherHelper helper;
	
	String testData = "Hello world!";
	
	@Test
	public void testDigest() {
		String digest = helper.toDigest(testData);
		
		String notValid = helper.toDigest("HelloWorld!");
		
		assertEquals(digest, "GcPnGr/XfMomG/VaLnDH8+8X8YHiMUnj4FGdx5VT5uU=");
		assertNotEquals(digest, notValid);
	}
	
	@Test
	public void testAes() {
		String encStr = helper.aesEncrypt(testData);
		assertEquals(helper.aesDecrypt(encStr), testData);
	}
	
	@Test
	public void testRsa() {
	}
}
