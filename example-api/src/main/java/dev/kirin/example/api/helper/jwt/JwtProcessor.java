package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

class JwtProcessor {
	protected static final String DELIMETER = ".";
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	@Getter(value = AccessLevel.PROTECTED)
	private JwtConfig config;
	
	protected JwtProcessor(@NonNull final JwtConfig config) {
		this.config = config;
	}
	
	protected final String getEncodedSignature(Algorithm alg, String secret, String content) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac hmac = Mac.getInstance(alg.getDigest());
		SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), alg.getDigest());
		hmac.init(keySpec);
		return base64Encode(hmac.doFinal(content.getBytes()));
	}
	
	protected final String base64Encode(byte[] content) {
		return Base64.encodeBase64URLSafeString(content).replaceAll("=", "");
	}
}
