package dev.kirin.example.api.helper.jwt.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtilTest {

	@Test
	public void testIsEmpty() {
		String value = null;
		
		assertEquals(StringUtil.isEmpty(value), true);
		
		value = "";
		assertEquals(StringUtil.isEmpty(value), true);
		
		value = "a";
		assertEquals(StringUtil.isEmpty(value), false);
	}
	
	@Test
	public void testIsEmptyDefault() {
		String value = null;
		String defaultValue = "default";
		
		assertEquals(StringUtil.isEmpty(value, defaultValue), defaultValue);
		
		value = "";
		assertEquals(StringUtil.isEmpty(value, defaultValue), defaultValue);
		
		value = "a";
		assertEquals(StringUtil.isEmpty(value, defaultValue), value);
	}
	
	@Test
	public void testConcat() {
		String valid = "abc.123";
		
		assertEquals(StringUtil.concat("abc", ".", "123"), valid);
	}
	
	@Test
	public void testRadomString() {
		int length = 64;
		String str = StringUtil.randomString(length);
		
		log.debug("radom string : {}", str);
		assertEquals(str.length(), length);
	}
}
