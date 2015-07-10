package com.skplanet.logsearch.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;

public class LogBulk {
	static  Logger  logger = Logger.getLogger(LogBulk.class);
	
	static public  int    BULK_PUT   = 0;
	static public  int    BULK_SEND  = 1;
	
	static private int    bulkUnitSize = 10;
	static private int    bulkInterval = 100;
	
	private List<String>  bulkList;
	private String        bulkIndex;
	private long          bulkTimeStamp;
	
	public LogBulk() {
		bulkList = new ArrayList<String>();
		bulkIndex    = "";
		bulkTimeStamp = System.currentTimeMillis();
	}
	
	public void processLog(LogObject logObj) {
		int op = check(logObj);
		
		if (op == BULK_SEND) {
			send();
		} else {
			put(logObj);
		}	
	}
	
	public void processIdle() {
		if (checkTimeout()) {
			send();
		}	
	}
	
	public int check(LogObject logObj) {		
		if (checkCount()) return BULK_SEND;
		
		if (checkIndex(logObj.getLogIndex())) return BULK_SEND;
		
		if (checkTimeout()) return BULK_SEND;
		
		return BULK_PUT;
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
