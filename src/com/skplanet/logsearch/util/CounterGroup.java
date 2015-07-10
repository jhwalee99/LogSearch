package com.skplanet.logsearch.util;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class CounterGroup {
	static Logger            logger;
	static public  Counter   counter[]; 
	static public  String    processName;
	static public  String    currentTime;
	static public  int       maxCount;
	static public  int       lastIdx;	
	static public  HashMap<String, Integer>   indexMap;
	
	static public void init(String name, int size) {
		//logger Info
		String loggerName = "counter.logger";
		if (System.getProperty(loggerName) == null) {
			loggerName = "logger";
		}		
		logger = Logger.getLogger(System.getProperty(loggerName));
		
		processName = name;
		maxCount    = size;
		lastIdx     = 0;
		counter     =  new Counter[maxCount];
		
		indexMap = new HashMap<String, Integer>();
	}
	
	static public Counter register(String title, int size) {
		Integer index;
		
		index = indexMap.get(title);
		if (index != null) {
			return counter[index.intValue()];
		}
		
		if (lastIdx == maxCount) {
			logger.error("SHOULD change counter group size (counterGroup.size :  " + maxCount + ")");
			return counter[lastIdx-1];
		}
		
		int current = lastIdx;
		lastIdx++;
		
		counter[current] = new Counter(title, size);		
		index = current;
		indexMap.put(title, index);
		
		
		return counter[current];
	}
	
	static public void display() {
		int last = lastIdx;
		String line = "";
		
		StringBuilder sb;
		StringBuilder sbAll = new StringBuilder();

		sbAll.append("\n");
		sbAll.append("------------------------------------------------------------------------------------------\n" );		
		line = String.format("   %-30s : [CURRENT : %-12s] [LAST : Hour/Day        ]\n", processName, currentTime);
		sbAll.append(line);	
		sbAll.append("------------------------------------------------------------------------------------------\n" );
		for (int i=0; i < last; i++) {
			sb = counter[i].display();
			sbAll.append(sb);
		}
		sbAll.append("------------------------------------------------------------------------------------------\n" );
		
		logger.info(sbAll);		
	}
	
	static public void reset(int hour) {
		int last = lastIdx;
		
		for (int i=0; i < last; i++) {
			counter[i].reset(hour);
		}
	}
	
	static public void setCurrentTime(String current) {		
		currentTime = current;
	}
	
	static public void shutdown() {
		logger.info("CounterGroup shutdown");
		display();
	}
}