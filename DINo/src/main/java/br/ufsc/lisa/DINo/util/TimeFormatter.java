package br.ufsc.lisa.DINo.util;

import java.util.concurrent.TimeUnit;

public abstract class TimeFormatter {
	
	public static String formatTime(long time) {
		long second = TimeUnit.MILLISECONDS.toSeconds(time);
		long minute = TimeUnit.MILLISECONDS.toMinutes(time);
		long hour = TimeUnit.MILLISECONDS.toHours(time);
		time -= TimeUnit.SECONDS.toMillis(second);
		
		return String.format("%02d:%02d:%02d:%d", hour, minute, second, time);
	}

}
