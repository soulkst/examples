package dev.kirin.example.api.helper.jwt;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasePayload {
	@JsonProperty(value = "jid")
	@JsonInclude(value = Include.NON_NULL)
	private String jwtId;
	
	@JsonProperty(value = "sub")
	@JsonInclude(value = Include.NON_NULL)
	private String subject;
	
	@JsonProperty(value = "aud")
	@JsonInclude(value = Include.NON_NULL)
	private Object audience;
	
	@JsonProperty(value = "iss")
	@JsonInclude(value = Include.NON_NULL)
	private String issuer;
	
	@JsonProperty(value = "exp")
	private Date expireAt;
	
	@JsonProperty(value = "nbf")
	@JsonInclude(value = Include.NON_NULL)
	private Long notBeforeAt;
	
	@JsonProperty(value = "iat")
	@JsonInclude(value = Include.NON_NULL)
	private Long issuedAt;
	
	public void setAudience(String audience) {
		this.audience = audience;
	}
	
	public void setAudience(String... audience) {
		this.audience = audience;
	}
}
