package dev.kirin.example.api.helper.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.kirin.example.api.helper.jwt.JwtSigner.JWTSignFailureException;
import dev.kirin.example.api.helper.jwt.JwtVerify.JWTVerifyError;
import dev.kirin.example.api.helper.jwt.JwtVerify.VerifyErroCause;
import lombok.extern.slf4j.Slf4j;

@DisplayName("JWT library TEST")
@Slf4j
public class JWTTest {
	private Algorithm algorithm = Algorithm.HS256;
	private String secret = "1234";
	
	// 1hour
	private long expireTime = 1 * 60 * 60 * 1000;
	
	
	private JwtSigner signer;
	private JwtVerify verifier;
	
	private BasePayload payload;
	
	@BeforeEach
	public void before() throws JWTSignFailureException {
		payload = new BasePayload();
		payload.setExpireAt(new Date(expireTime + System.currentTimeMillis()));
		payload.setSubject("Test subject");
		
		signer = new JwtSigner()
				.withHeader(algorithm);
		
		verifier = new JwtVerify()
			.prepare(algorithm);
	}
	
	@Test
	public void testNormal() throws JWTSignFailureException, JWTVerifyError {
		String token = signer.sign(payload, secret);
		
		log.info("Generated token : {}", token);
		
		verifier.verify(token, secret);
	}
	
	
	@Test
	public void testExpired() throws JWTSignFailureException {
		payload.setExpireAt(new Date(System.currentTimeMillis()));
		
		String token = signer.sign(payload, secret);
		
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
		payload.setNotBeforeAt(expireTime);
		
		String token = signer.sign(payload, secret);
		
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
		String token = signer.sign(payload, secret);
		
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.prepare(algorithm, "test")
				.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.NOT_VALID_ISSUER) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.NOT_VALID_ISSUER);
	}
	
	@Test
	public void testInvalidSecret() throws JWTSignFailureException {
		String token = signer.sign(payload, secret);
		String secret = "another_secret";
		
		JWTVerifyError expectedError = assertThrows(JWTVerifyError.class, () -> {
			verifier.verify(token, secret);
		});
		
		if(expectedError.getErrorCause() != VerifyErroCause.NOT_VALID) {
			log.error("error message", expectedError);
		}
		assertEquals(expectedError.getErrorCause(), VerifyErroCause.NOT_VALID);
	}
	
	
}
