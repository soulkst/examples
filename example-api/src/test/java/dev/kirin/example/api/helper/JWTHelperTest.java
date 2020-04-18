package dev.kirin.example.api.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.kirin.example.api.helper.jwt.BasePayload;
import dev.kirin.example.api.helper.jwt.JwtSigner.JWTSignFailureException;
import dev.kirin.example.api.helper.jwt.JwtVerify.JWTVerifyError;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class JWTHelperTest {

	@Autowired
	private JWTHelper jwt;
	
	private BasePayload data;
	
	@BeforeEach
	public void before() {
		data = new BasePayload();
		data.setSubject("Test User");
	}
	
	@Test
	public void testGenerate() throws JWTSignFailureException {
		String token = jwt.generate(data);
		log.info("[testGenerate] generated token : {}", token);
	}
	
	@Test
	public void testVerify() throws JWTSignFailureException, JWTVerifyError {
		String token = jwt.generate(data);
		log.info("[testVerify] generated token : {}", token);
		jwt.verify(token);
	}
}
