package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.commons.codec.binary.Base64;

import com.fasterxml.jackson.core.type.TypeReference;

import dev.kirin.example.api.helper.jwt.util.StringUtil;
import lombok.Getter;
import lombok.NonNull;

public class JwtVerify extends JwtProcessor {
	
	public JwtVerify(@NonNull final JwtConfig config) {
		super(config);
	}
	
	public <T extends BasePayload> T verify(@NonNull String token, @NonNull String secret) throws JWTVerifyError {
		String[] items = token.split("\\" + DELIMETER);

		try {
			String headerPart = items[0];
			String claimPart = items[1];
			
			Header header = mapper.readValue(Base64.decodeBase64(headerPart), Header.class);
			T claim = mapper.readValue(Base64.decodeBase64(claimPart), new TypeReference<T>() {});
			
			String type = StringUtil.isEmpty(getConfig().getType(), JwtConfig.TYPE_JWT);
			if(!type.equals(header.getType())) {
				throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID, "'type' missmatch.");
			}
			
			try {
				String headerAndClaim = StringUtil.concat(headerPart, DELIMETER, claimPart);
				String signature = getEncodedSignature(getConfig().getAlgorithm(), secret, headerAndClaim);
				if(!signature.equals(items[2])) {
					throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID, "'sigunature' is not valid.");
				}
			} catch (InvalidKeyException | NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(getConfig().getIssuer() != null && !getConfig().getIssuer().equals(claim.getIssuer())) {
				throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID_ISSUER);
			}
			
			if(claim.getIssuer() != null && !claim.getIssuer().equals(getConfig().getIssuer())) {
				throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID_ISSUER);
			}
			
			if(claim.getExpireAt() < System.currentTimeMillis()) {
				throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.EXPIRED);
			}
			
			if(claim.getNotBeforeAt() != null) {
				if(claim.getIssuedAt() == null) {
					throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID, "'IssuedAt' field is empty.");
				}
				
				GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
				cal.setTimeInMillis(claim.getIssuedAt());
				cal.add(Calendar.SECOND, claim.getNotBeforeAt());
				
				if(cal.getTimeInMillis() > System.currentTimeMillis()) {
					throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_YET, "Can use after " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime()));
				}
			}
			return claim;
		} catch (JWTVerifyError e) {
			throw e;
		} catch (Exception e) {
			throw new JWTVerifyError(JWTVerifyError.VerifyErroCause.NOT_VALID, e);
		}
	}
	
	public static class JWTVerifyError extends Exception {
		public static enum VerifyErroCause {
			NOT_VALID, EXPIRED, NOT_YET, NOT_VALID_ISSUER
		}
		
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
