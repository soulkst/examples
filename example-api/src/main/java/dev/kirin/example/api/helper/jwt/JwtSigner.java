package dev.kirin.example.api.helper.jwt;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.kirin.example.api.helper.jwt.util.StringUtil;
import dev.kirin.example.api.helper.jwt.util.TimeUtil;
import lombok.NonNull;

public class JwtSigner extends JwtProcessor {
	private boolean isNbfEnable = false;

	protected JwtSigner(@NonNull final JwtConfig config) {
		super(config);
	}
	
	public JwtSigner withEnableNotBeforeAt() {
		isNbfEnable = true;
		return this;
	}
	
	public <T extends BasePayload> String sign(@NonNull T payload, @NonNull String secret) throws JWTSignFailureException {
		try {
			String header = base64Encode(mapper.writeValueAsBytes(
					new Header(getConfig().getAlgorithm(), StringUtil.isEmpty(getConfig().getType(), JwtConfig.TYPE_JWT)
					)));
			
			long now = TimeUtil.now();
			
			if(payload.getIssuedAt() == null) {
				payload.setIssuedAt(now);
			}
			
			payload.setExpireAt(TimeUtil.after(now, getConfig().getExpireUnit().getValue(), getConfig().getExpireTime()));
			
			if(isNbfEnable) {
				payload.setNotBeforeAt((int) TimeUtil.after(now, getConfig().getNotBeforeAtUnit().getValue(), getConfig().getNotBeforeTime()));
			}
			
			if(!StringUtil.isEmpty(getConfig().getIssuer())) {
				payload.setIssuer(getConfig().getIssuer());
			}
			
			String claim = base64Encode(mapper.writeValueAsBytes(payload));
			
			String headerAndClaim = StringUtil.concat(header, DELIMETER, claim);
			String signature = getEncodedSignature(getConfig().getAlgorithm(), secret, headerAndClaim);
			
			return StringUtil.concat(headerAndClaim, DELIMETER, signature);
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
