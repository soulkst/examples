package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

class JwtProcessor {
	protected static final String DELIMETER = ".";
	
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	protected final String getEncodedSignature(Algorithm alg, String secret, String content) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac hmac = Mac.getInstance(alg.getDigest());
		SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), alg.getDigest());
		hmac.init(keySpec);
		return Base64.encodeBase64String(hmac.doFinal(content.getBytes()));
	}
}
