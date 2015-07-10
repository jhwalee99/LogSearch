package com.skplanet.logsearch.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;

public class LogStreamManager {
	static  Logger  logger = Logger.getLogger(LogStreamManager.class);
		
	static private int    bulkUnitSize = 10;
	static private int    bulkInterval = 100;
	
	private HashMap<String, LogStream> streamMap = new HashMap<String, LogStream>();
	private LogParser     logParser = new LogParser();
	private List<String>  bulkList;
	private String        bulkIndex;
	private long          bulkTimeStamp;
	
	public LogStreamManager() {
	
	}
	
	
	public String processLog(String log) {
		
		logParser.parse(log);
		
		String  logId     = logParser.getLogId();
		String  logBody   = logParser.getLogBody();
		String  outputLog = null;
		boolean flagMulti = logParser.isMulti();
		
		LogStream stream = streamMap.get(logId);
		
		if (stream != null) {
			if (flagMulti == false) {
				outputLog = stream.getStream();
				stream.set(logBody);
			} else {
				stream.add(logBody);
			}
		} else {			
			stream = new LogStream(logId);
			stream.add(logBody);
		}
		
		return outputLog;
	}
	
	public List<String> processIdle() {
		List<String> timeoutList = new ArrayList<String>();
		
		for(LogStream logStream : streamMap.values()) {
			if (logStream.checkTimeout() ==  true) {
				timeoutList.add(logStream.getStream());
				logStream.clear();
			}
		}
		
		return timeoutList;
	}
	
	public int check() {		
		return 1;
	}
	
	public boolean checkCount() {
		if (bulkList.size() >= bulkUnitSize) return true;
		
		return false;
	}
	
	public boolean checkIndex(String index) {
		if (bulkIndex.length() == 0) return false;

		if (index.compareToIgnoreCase(bulkIndex) != 0) return true;
		
		return false;
	}
	
	public boolean checkTimeout() {
		long timeStamp = System.currentTimeMillis();
		
		if ((timeStamp - bulkTimeStamp) > bulkInterval)	return true;
		
		return false;
	}
	
	public String getLogType() {
		return "Test";
	}
	
	public void put(LogObject logObject) {
		String index = logObject.getLogIndex();
		String json  = logObject.toString();
		
		if (bulkIndex.length() == 0) bulkIndex = index;
		
		bulkList.add(json);
	}
	
	/**
	 * send bulk logs to ElasticSearch 
	 * 
	 * @param messages Log(Json format)  
	 */
	public void send()
	{	
		if (bulkList.size() == 0) return;
		
		//////////////////////////////////////////////////////////		
		// send ElasticSearch
		//////////////////////////////////////////////////////////		
		String logType  = getLogType();
		ElasticsearchAPI.insertBulk(bulkIndex, logType, bulkList);
		//////////////////////////////////////////////////////////		
		
		//reset bulk list
		bulkTimeStamp = System.currentTimeMillis();
		bulkList.clear();
		bulkIndex = "";
	}
}
