package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.validation.constraints.NotNull;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.NonNull;

public class JwtSigner extends JwtProcessor {
	private String header;
	private String claim;
	
	private Algorithm alg;
	
	public JwtSigner withHeader(@NonNull JwtConfig config) throws JWTSignFailureException {
		return withHeader(new Header(config.getAlgorithm(), config.getType()));
	}
	
	public JwtSigner withHeader(@NotNull Algorithm alg, String type) throws JWTSignFailureException {
		return withHeader(new Header(alg, type));
	}
	
	public JwtSigner withHeader(@NotNull Algorithm alg) throws JWTSignFailureException {
		return withHeader(new Header(alg, JwtConfig.TYPE_JWT));
	}
	
	public JwtSigner withHeader(@NotNull Header header) throws JWTSignFailureException {
		try {
			this.alg = header.getAlgorithm();
			this.header = Base64.encodeBase64URLSafeString(mapper.writeValueAsBytes(header));
		} catch (JsonProcessingException e) {
			throw new JWTSignFailureException("Cannot convert 'header' to json.");
		}
		return this;
	}
	
	public <T extends BasePayload> String sign(@NonNull T payload, @NonNull String secret) throws JWTSignFailureException {
		try {
			if(header == null || "".equals(header)) {
				throw new JWTSignFailureException("'Header' is not setted.");
			}
			
			if(payload.getIssuedAt() == null) {
				payload.setIssuedAt(System.currentTimeMillis());
			}
			
			this.claim = Base64.encodeBase64URLSafeString(mapper.writeValueAsBytes(payload));
			
			StringBuffer buffer = new StringBuffer();
			buffer.append(header).append(DELIMETER).append(claim);
			String signature = getEncodedSignature(alg, secret, buffer.toString());
			buffer.append(DELIMETER).append(signature);
			return buffer.toString();
		} catch (InvalidKeyException | IllegalStateException | NoSuchAlgorithmException | JsonProcessingException e) {
			throw new JWTSignFailureException("Cannot make token.", e);
		}
	}
	
	public static class JWTSignFailureException extends Exception {
		private static final long serialVersionUID = -5676000299411002710L;

		public JWTSignFailureException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public JWTSignFailureException(String message, Throwable cause, boolean enableSuppression,
				boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		public JWTSignFailureException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public JWTSignFailureException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public JWTSignFailureException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}
	}
}
