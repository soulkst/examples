package dev.kirin.example.api.helper.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.kirin.example.api.helper.jwt.JwtSigner.JWTSignFailureException;
import dev.kirin.example.api.helper.jwt.JwtVerify.JWTVerifyError;
import dev.kirin.example.api.helper.jwt.JwtVerify.JWTVerifyError.VerifyErroCause;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@DisplayName("JWT library TEST")
@Slf4j
public class JWTTest {
	private String secret = "1234";
	
	private Config config;
	
	private JwtSigner signer;
	private JwtVerify verifier;
	
	private BasePayload payload;
	
	@BeforeEach
	public void before() throws JWTSignFailureException {
		config = new Config();
		config.setAlgorithm(Algorithm.HS256);
		config.setIssuer("testIssuer");
		config.setExpireUnit(TimeUnit.HOUR);
		config.setExpireTime(1);
		config.setNotBeforeAtUnit(TimeUnit.MIN);
		config.setNotBeforeTime(10);
		
		payload = new BasePayload();
		payload.setSubject("Test subject");
		
	}
	
	@Test
	public void testNormal() throws JWTSignFailureException, JWTVerifyError, InterruptedException {
		signer = new JwtSigner(config);
		verifier = new JwtVerify(config);
		
		String token = signer.sign(payload, secret);
		
		log.info("[testNormal] Generated token : {}", token);
		
		verifier.verify(token, secret);
	}
	
	
	@Test
	public void testExpired() throws JWTSignFailureException, InterruptedException {
		config.setExpireUnit(TimeUnit.SECOND);
		config.setExpireTime(1);
		
		signer = new JwtSigner(config);
		verifier = new JwtVerify(config);
		
		String token = signer.sign(payload, secret);
		
		log.info("[testExpired] Generated token : {}", token);
		
		Thread.sleep(1 * 1000);
		
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.EXPIRED) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.EXPIRED);
	}
	
	@Test
	public void testNotBeforeAt() throws JWTSignFailureException {
		signer = new JwtSigner(config);
		verifier = new JwtVerify(config);
		
		String token = signer
				.withEnableNotBeforeAt()
				.sign(payload, secret);
		
		log.info("[testNotBeforeAt] Generated token : {}", token);
		
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.NOT_YET) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.NOT_YET);
	}
	
	@Test
	public void testInvalidIssuer() throws JWTSignFailureException {
		signer = new JwtSigner(config);
		String token = signer.sign(payload, secret);
		
		log.info("[testInvalidIssuer] Generated token : {}", token);
		
		config.setIssuer("invalidIssuer");
		verifier = new JwtVerify(config);
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.NOT_VALID_ISSUER) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.NOT_VALID_ISSUER);
	}
	
	@Test
	public void testInvalidSecret() throws JWTSignFailureException {
		signer = new JwtSigner(config);
		verifier = new JwtVerify(config);
		
		String token = signer.sign(payload, secret);
		
		log.info("[testInvalidSecret] Generated token : {}", token);
		
		String secret = "another_secret";
		
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.NOT_VALID) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.NOT_VALID);
	}
	
	@Setter
	@Getter
	class Config implements JwtConfig {
		private Algorithm algorithm;
		private String type;
		private String issuer;
		
		private TimeUnit expireUnit;
		private int expireTime;
		
		private TimeUnit notBeforeAtUnit;
		private int notBeforeTime;
	}
}
