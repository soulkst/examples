package dev.kirin.example.api.helper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class CipherHelper {
	private static final int IV_LENGTH = 16;
	// TODO: ebc vs cbc
	private static final String AES_CIPHER_INSTANCE_TYPE = "AES/CBC/PKCS5Padding";
	
	private String ALGORITHM_AES = "AES";
	private String ALGORITHM_RSA = "RSA";
	
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	@Value("${data_enc.digest.type}")
	private DigestType digestType;
	@Value("#{'${data_enc.digest.secret}'.bytes}")
	private byte[] digestSecret;
	
	@Value("${data_enc.aes.type}")
	private int bitBlock;
	@Value("#{'${data_enc.aes.secret}'.bytes}")
	private byte[] aesSecret;
	
	private DigestType shaTypeForAes;
	
	@Value("${data_enc.rsa.private_key:@null}.bytes")
	private byte[] privateKey;
	@Value("${data_enc.rsa.public_key:@null}.bytes")
	private byte[] publicKey;
	
	
	@PostConstruct
	public void init() {
		shaTypeForAes = DigestType.valueOf("SHA" + bitBlock);
	}
	
	/**
	 * get hmac hash string encoded base64
	 * @param str
	 * @return
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 */
	public String toDigest(String data) {
		return base64Encode(doDigest(digestType, getBytes(data)));
	}
	
	/**
	 * get encrypted aes string
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	public String aesEncrypt(String data) {
		try {
			return base64Encode(doAes(Cipher.ENCRYPT_MODE, getBytes(data)));
		} catch (Exception e) {
			throw new CipherException(e);
		}
	}
	
	/**
	 * get decrypted aes string
	 * @param data
	 * @return
	 * @throws Exception 
	 */
	public String aesDecrypt(String data) {
		try {
			return getDataString(doAes(Cipher.DECRYPT_MODE, base64Decode(data)));
		} catch (Exception e) {
			throw new CipherException(e);
		}
	}
	
	/**
	 * encrypt rsa by public key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String rsaEncrypt(String data) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
			PublicKey key = keyFactory.generatePublic(keySpec);
			
			return base64Encode(doRsa(Cipher.DECRYPT_MODE, key, getBytes(data)));
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
			throw new CipherException(e);
		}
	}
	
	/**
	 * decrypt rsa by private key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public String rsaDecrypt(String data) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(publicKey);
			PrivateKey key = keyFactory.generatePrivate(keySpec);
			
			return getDataString(doRsa(Cipher.DECRYPT_MODE, key, base64Decode(data)));
		} catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException e) {
			throw new CipherException(e);
		}
	}
	
	public byte[] doDigest(DigestType type, byte[] data) {
		try {
			Mac mac = Mac.getInstance(type.getDigest());
			SecretKeySpec spec = new SecretKeySpec(digestSecret, type.getDigest());
			mac.init(spec);
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new CipherException(e);
		}
	}
	
	private byte[] doRsa(int mode, Key key, byte[] data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
		cipher.init(mode, key);
		return cipher.doFinal(data);
	}
	
	private byte[] doAes(int mode, byte[] data) throws Exception {
		IvParameterSpec iv = new IvParameterSpec(new byte[IV_LENGTH]);
		byte[] secretHash = doDigest(shaTypeForAes, aesSecret);
		SecretKeySpec secretSpec = new SecretKeySpec(secretHash, ALGORITHM_AES);
		
		Cipher cipher = Cipher.getInstance(AES_CIPHER_INSTANCE_TYPE);
		cipher.init(mode, secretSpec, iv);
		return cipher.doFinal(data);
	}
	
	private String base64Encode(byte[] data) {
		return getDataString(Base64.getEncoder().encode(data));
	}
	
	private byte[] base64Decode(String data) {
		return Base64.getDecoder().decode(data);
	}
	
	private byte[] getBytes(String str) {
		return str.getBytes(DEFAULT_CHARSET);
	}
	
	private String getDataString(byte[] data) {
		return new String(data, DEFAULT_CHARSET);
	}
	
	public static enum DigestType {
		SHA1("HmacSha1"), SHA128("HmacSha1")
		, SHA256("HmacSHA256"), SHA384("HmacSHA384"), SHA512("HmacSHA256")
		;
		
		@Getter
		private String digest;
		
		private DigestType(String digest) {
			this.digest = digest;
		}
	}
	
	public static class CipherException extends RuntimeException {
		private static final long serialVersionUID = 6706678939243576122L;

		public CipherException() {
			super();
			// TODO Auto-generated constructor stub
		}

		public CipherException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
			// TODO Auto-generated constructor stub
		}

		public CipherException(String message, Throwable cause) {
			super(message, cause);
			// TODO Auto-generated constructor stub
		}

		public CipherException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}

		public CipherException(Throwable cause) {
			super(cause);
			// TODO Auto-generated constructor stub
		}
	}
}
