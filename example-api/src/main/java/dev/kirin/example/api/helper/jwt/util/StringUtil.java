package dev.kirin.example.api.helper.jwt.util;

import java.util.Random;

public class StringUtil {
	public static final String BLANK = "";
	
	public static final boolean isEmpty(String str) {
		return (str == null || BLANK.equals(str));
	}
	
	public static final String isEmpty(String str, String defaultValue) {
		return isEmpty(str) ? defaultValue : str;
	}
	
	public static final String concat(Object... objs) {
		StringBuffer buffer = new StringBuffer();
		for(Object obj : objs) {
			buffer.append(obj);
		}
		return buffer.toString();
	}
	
	public static final String randomString(int length) {
		int min = '!';
		return new Random().ints(min, Byte.MAX_VALUE - 1)
			.limit(length)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();
	}
}
