package com.clock.webhook.utils;

import java.util.concurrent.TimeUnit;

public class ClockUtils {

	public static final String SECOND = "S";
	public static final String MINUTE = "M";
	public static final String HOUR = "H";

	/**
	 * Convert the unit value to a {@link TimeUnit}
	 * 
	 * @param unit
	 * @return {@link TimeUnit}
	 */
	public static TimeUnit getTimeUnitBy(String unit) {

		if (unit.equals(SECOND))
			return TimeUnit.SECONDS;

		if (unit.equals(MINUTE))
			return TimeUnit.MINUTES;

		else if (unit.equals(HOUR))
			return TimeUnit.HOURS;

		else
			return TimeUnit.SECONDS;

	}

}
