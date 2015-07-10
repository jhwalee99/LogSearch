package com.skplanet.logsearch.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class TimeUtil {
	static Logger logger = Logger.getLogger(System.getProperty("logger"));
	static public long   TIMECONVERT_ERROR = -1;
	
	static public long str2Long(String timeStr, String format, String timezone) {
		long timestamp = TIMECONVERT_ERROR;
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		Date dateTime;
		
		try {
			dateTime  = dateFormat.parse(timeStr);
			timestamp = dateTime.getTime();
		} catch (ParseException e) {
			logger.info("Time Convert Error : " + e.getMessage());
		}	
				
		return timestamp;
	}
	
	static public String long2Str(long timestamp, String format, String timezone) {
		String timeStr = "";
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		timeStr = dateFormat.format(timestamp);	
		
		return timeStr;
	}
	
	static public long addTime(long time, String intervalStr) {
		long returnTime  = 0;
		int  calInterval = Calendar.HOUR;
		Date date = new Date();		
		date.setTime(time);
		
		if (intervalStr.compareToIgnoreCase("minutely") == 0) calInterval = Calendar.MINUTE;
		else if (intervalStr.compareToIgnoreCase("hourly") == 0) calInterval = Calendar.HOUR;
		else if (intervalStr.compareToIgnoreCase("daily") == 0) calInterval = Calendar.DAY_OF_YEAR;
		else if (intervalStr.compareToIgnoreCase("monthly") == 0) calInterval = Calendar.MONTH;
		
		Calendar calendar = new GregorianCalendar();
		 
		calendar.setTime(date);
		calendar.add(calInterval, 1);
		returnTime = calendar.getTime().getTime();
		
		return returnTime;
	}
	
	static public long subsTime(long time, String intervalStr) {
		long returnTime  = 0;
		int  calInterval = Calendar.HOUR;
		
		Date date = new Date();		
		date.setTime(time);
		
		if (intervalStr.compareToIgnoreCase("minutely") == 0) calInterval = Calendar.MINUTE;
		else if (intervalStr.compareToIgnoreCase("hourly") == 0) calInterval = Calendar.HOUR;
		else if (intervalStr.compareToIgnoreCase("daily") == 0) calInterval = Calendar.DAY_OF_YEAR;
		else if (intervalStr.compareToIgnoreCase("monthly") == 0) calInterval = Calendar.MONTH;
		
		Calendar calendar = new GregorianCalendar();
		 
		calendar.setTime(date);
		calendar.add(calInterval, -1);
		returnTime = calendar.getTime().getTime();
		
		return returnTime;
	}
	
	static public String currentStr(String format, String timezone) {
		String timeStr = "";
		long   timestamp;
		
		timestamp = System.currentTimeMillis();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		timeStr = dateFormat.format(timestamp);	
		
		return timeStr;
	}
}
