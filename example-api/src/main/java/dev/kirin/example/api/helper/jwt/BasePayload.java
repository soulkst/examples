package dev.kirin.example.api.helper.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	@Setter(value = AccessLevel.PACKAGE)
	private Long expireAt;
	
	@JsonProperty(value = "nbf")
	@JsonInclude(value = Include.NON_NULL)
	private Integer notBeforeAt;
	
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
