package com.skplanet.logsearch.log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;

public class LogParser {
	String logId;
	String logBody;
	boolean flagMulti;
	
	public LogParser() {
		//TODO
	}
	
	public void parse(String log) {
		
	}
	
	public String getLogId() {
		return logId;
	}
	
	public String getLogBody() {
		return logBody;
	}
	
	public boolean isMulti() {
		return flagMulti;
	}
}
