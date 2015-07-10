package com.skplanet.logsearch.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;

public class LogStream {
	final   static int MULTI_LOG_INTERVAL = 10; //milliseconds
	static  Logger  logger = Logger.getLogger(LogStream.class);

	private String        logId;
	private StringBuffer  logBuffer = new StringBuffer();
	private long          timeStamp;
	
	public LogStream(String id) {
		logId = id;
		timeStamp = System.currentTimeMillis();
	}
	
	public void add(String log) {
		timeStamp = System.currentTimeMillis();
		logBuffer.append(log);
	}
	
	public void set(String log) {
		timeStamp = System.currentTimeMillis();
		logBuffer.setLength(0);
		logBuffer.append(log);
	}
	
	public String getStream() {
		return logBuffer.toString();
	}
	
	public void clear() {
		timeStamp = -1;
		logBuffer.setLength(0);
	}

	public boolean checkTimeout() {
		if (timeStamp < 0) return false;
		
		long diff = System.currentTimeMillis() - timeStamp;
		
		if (diff > MULTI_LOG_INTERVAL)	return true;
		
		return false;
	}
	
	public String getLogId() {
		return logId;
	}
}
