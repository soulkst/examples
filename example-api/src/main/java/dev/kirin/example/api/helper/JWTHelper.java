package dev.kirin.example.api.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import dev.kirin.example.api.helper.jwt.Algorithm;
import dev.kirin.example.api.helper.jwt.BasePayload;
import dev.kirin.example.api.helper.jwt.JwtConfig;
import dev.kirin.example.api.helper.jwt.JwtSigner;
import dev.kirin.example.api.helper.jwt.JwtSigner.JWTSignFailureException;
import dev.kirin.example.api.helper.jwt.JwtVerify;
import dev.kirin.example.api.helper.jwt.JwtVerify.JWTVerifyError;
import dev.kirin.example.api.helper.jwt.TimeUnit;
import lombok.Getter;
import lombok.Setter;

@Component
public class JWTHelper {
	@Autowired
	private Config config;
	
	@Value("${jwt.secret}")
	private String secret;
	
	public <T extends BasePayload> String generate(T data) throws JWTSignFailureException {
		return new JwtSigner(config).sign(data, secret);
	}
	
	public <T extends BasePayload> T verify(String token) throws JWTVerifyError {
		return new JwtVerify(config).verify(token, secret);
	}
	
	@Component
	@ConfigurationProperties(prefix = "jwt")
	@Getter
	@Setter
	static final class Config implements JwtConfig {
		private Algorithm algorithm;
		@Value("${type:#{T(dev.kirin.example.api.helper.jwt.JwtConfig).TYPE_JWT}}")
		private String type;
		private String issuer;
		
		@Value("${expire.unit:DAY}")
		private TimeUnit expireUnit;
		@Value("${expire.time:1}")
		private int expireTime;
		
		@Value("${notbefore.unit:#{null}}")
		private TimeUnit notBeforeAtUnit;
		@Value("${notBefore.time:-1}")
		private int notBeforeTime;
	}
	
}
