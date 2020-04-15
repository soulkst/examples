package dev.kirin.example.api.helper.jwt;

import java.util.Calendar;

import lombok.Getter;

@Getter
public enum TimeUnit {
	SECOND(Calendar.SECOND)
	, MIN(Calendar.MINUTE)
	, HOUR(Calendar.HOUR_OF_DAY)
	, DAY(Calendar.DAY_OF_YEAR)
	, MONTH(Calendar.MONTH)
	, YEAR(Calendar.YEAR)
	;
	
	private int value;
	
	private TimeUnit(int value) {
		this.value = value;
	}
}
