package dev.kirin.example.api.helper.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Header {
	@JsonProperty(value = "alg")
	private Algorithm algorithm;
	@JsonProperty(value = "typ")
	private String type;
}
