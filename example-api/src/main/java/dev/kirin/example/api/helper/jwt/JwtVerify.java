package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.Getter;
import lombok.NonNull;

public class JwtVerify extends JwtProcessor {
	private Algorithm alg;
	private String issuer;
	private String type = JwtConfig.TYPE_JWT;
	
	public JwtVerify prepare(@NonNull JwtConfig config) {
		return prepare(config.getAlgorithm(), config.getType(), config.getIssuer());
	}
	
	public JwtVerify prepare(@NonNull Algorithm alg) {
		this.alg = alg;
		return this;
	}
	
	public JwtVerify prepare(@NonNull Algorithm alg, @NonNull String issuer) {
		this.alg = alg;
		this.issuer = issuer;
		return this;
	}
	
	public JwtVerify prepare(@NonNull Algorithm alg, @NonNull String type, @NonNull String issuer) {
		this.alg = alg;
		this.type = type;
		this.issuer = issuer;
		return this;
	}
	
	public <T extends BasePayload> T verify(@NonNull String token, @NonNull String secret) throws JWTVerifyError {
		String[] items = token.split("\\" + DELIMETER);

		try {
			String headerPart = items[0];
			String claimPart = items[1];
			
			Header header = mapper.readValue(Base64.decodeBase64(headerPart), Header.class);
			T claim = mapper.readValue(Base64.decodeBase64(claimPart), new TypeReference<T>() {});
			
			if(!type.equals(header.getType())) {
				throw new JWTVerifyError(VerifyErroCause.NOT_VALID, "'type' missmatch.");
			}
			
			try {
				StringBuffer buffer = new StringBuffer();
				buffer.append(headerPart).append(DELIMETER).append(claimPart);
				
				String signature = getEncodedSignature(alg, secret, buffer.toString());
				if(!signature.equals(items[2])) {
					throw new JWTVerifyError(VerifyErroCause.NOT_VALID, "'sigunature' is not valid.");
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(this.issuer != null && !this.issuer.equals(claim.getIssuer())) {
				throw new JWTVerifyError(VerifyErroCause.NOT_VALID_ISSUER);
			}
			
			if(claim.getIssuer() != null && !claim.getIssuer().equals(this.issuer)) {
				throw new JWTVerifyError(VerifyErroCause.NOT_VALID_ISSUER);
			}
			
			if(claim.getExpireAt().getTime() < System.currentTimeMillis()) {
				throw new JWTVerifyError(VerifyErroCause.EXPIRED);
			}
			
			if(claim.getNotBeforeAt() != null) {
				if(claim.getIssuedAt() == null) {
					throw new JWTVerifyError(VerifyErroCause.NOT_VALID, "'IssuedAt' field is empty.");
				}
				long nbfValue = claim.getIssuedAt() + claim.getNotBeforeAt();
				if(nbfValue > System.currentTimeMillis()) {
					throw new JWTVerifyError(VerifyErroCause.NOT_YET);
				}
			}
			return claim;
		} catch (JWTVerifyError e) {
			throw e;
		} catch (Exception e) {
			throw new JWTVerifyError(VerifyErroCause.NOT_VALID, e);
		}
	}
	
	public static enum VerifyErroCause {
		NOT_VALID, EXPIRED, NOT_YET, NOT_VALID_ISSUER
	}
	
	public static class JWTVerifyError extends Exception {
		private static final long serialVersionUID = -4619834783343555502L;

		@Getter
		private VerifyErroCause errorCause;
		
		public JWTVerifyError(VerifyErroCause errorCause) {
			super();
			this.errorCause = errorCause;
		}
		
		public JWTVerifyError(VerifyErroCause errorCause, String message) {
			super(message);
			this.errorCause = errorCause;
		}

		public JWTVerifyError(VerifyErroCause errorCause, Throwable cause) {
			super(cause);
			this.errorCause = errorCause;
		}
	}
}
