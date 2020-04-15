package dev.kirin.example.api.helper.jwt;

public interface JwtConfig {
	public static final String TYPE_JWT = "JWT";
	
	Algorithm getAlgorithm();
	String getType();
	String getIssuer();
	
	TimeUnit getExpireUnit();
	int getExpireTime();
	
	TimeUnit getNotBeforeAtUnit();
	int getNotBeforeTime();
}
