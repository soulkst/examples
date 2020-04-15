package dev.kirin.example.api.helper.jwt;

import lombok.Getter;

@Getter
public enum Algorithm {
	HS256("HmacSHA256"), HS384("HmacSHA384"), HS512("HmacSHA512")
	;
	
	private String digest;
	
	private Algorithm(String digest) {
		this.digest = digest;
	}
}
