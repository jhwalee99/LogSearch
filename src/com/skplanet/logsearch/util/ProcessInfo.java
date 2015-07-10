package com.skplanet.logsearch.util;

import org.apache.log4j.Logger;

public class ProcessInfo {
	static Logger          logger;
	static private long    startupTime;
	static private String  startupTimeStr;
	static private String  processName;
	static private Counter processCounter;
	
	static public void init(String name) {
		//logger Info
		String loggerName = "counter.logger";
		if (System.getProperty(loggerName) == null) {
			loggerName = "logger";
		}		
		logger = Logger.getLogger(System.getProperty(loggerName));
		
		startupTime = System.currentTimeMillis();
		startupTimeStr = TimeUtil.long2Str(startupTime, "yyyy-MM-dd HH:mm:ss", "GMT+9");	
		
		processName = name;
		
		processCounter = new Counter("Status", 128);
	}
	
	static public int register(String name) {
		int index;
		index = processCounter.register(name);
		return index;
	}
	
	static public void inc(int index) {
		processCounter.inc(index);
	}
	
	static public void inc(int index, int count) {
		processCounter.inc(index, count);
	}

	static public void dec(int index) {
		processCounter.dec(index);
	}
	
	static public void display () {
		long diffTime = System.currentTimeMillis() - startupTime;
		        
		long diffSeconds = (diffTime / 1000) % 60;
        long diffMinutes = (diffTime / (60 * 1000)) % 60;
        long diffHours   = (diffTime / (60 * 60 * 1000)) % 24;
        long diffDays    = diffTime / (24 * 60 * 60 * 1000);
        
        String line;
		StringBuilder lines = new StringBuilder();
		StringBuilder sb;
		
		lines.append("\n");
		lines.append("------------------------------------------------------------------------------------------\n" );
		line = String.format("   %s : [Startup : %-20s (%dd %2dh %2dm %2ds)]\n", processName, startupTimeStr, diffDays, diffHours, diffMinutes, diffSeconds);
		lines.append(line);
		lines.append("------------------------------------------------------------------------------------------\n" );
		sb = processCounter.displayCurrent();
		lines.append(sb);
		lines.append("------------------------------------------------------------------------------------------\n" );
		
		logger.info(lines);
	}
	
	static public void shutdown() {
		logger.info("ProcessInfo shutdown");
		display();
	}	
}
