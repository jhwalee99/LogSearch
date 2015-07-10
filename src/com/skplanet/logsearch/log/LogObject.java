package com.skplanet.logsearch.log;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.skplanet.logsearch.util.Counter;
import com.skplanet.logsearch.util.CryptoHash;
import com.skplanet.logsearch.util.TimeUtil;

/** 
* Log Object
*  
* @author JongHwa Lee (SKP)
* @version 0.1 
*   
*/
@SuppressWarnings("unchecked")
public class LogObject {
	static Logger logger = Logger.getLogger(System.getProperty("logger"));
	static String LOG_INDEX = "log-";
	 
	//////////////////////////////////////////////////////////
	// Validation Info
	//////////////////////////////////////////////////////////		
	public Boolean isValid;
	
	//////////////////////////////////////////////////////////
	// CUE Log Info
	//////////////////////////////////////////////////////////
	public long    esTimestamp;	
	public long    cueTimestamp;
	
	public String  cueTimestampString;
	
	//////////////////////////////////////////////////////////
	// ElasticSearch Info
	//////////////////////////////////////////////////////////
	public String     json;
	public String     metricType;
	public String     metricService;
	public String     metricHost;
	public String	  metricLogObject;
	
	/**
	 * parse Common Log and make ES Json info
	 * 
	 * @param json Log(Json format)  
	 */
	public LogObject(String json) {
		isValid = true;
		
		
	}

	/**
	 * convert CUE --> Elasticsearch : Common Field
	 * 
	 */
	public void convertTimestamp() {	
		String esTimestampString = "";

		// get UTC from CUE timestamp
		esTimestamp = TimeUtil.str2Long(cueTimestampString, "yyyyMMddHHmmss", "GMT+9");
		if (esTimestamp == TimeUtil.TIMECONVERT_ERROR) {
			logger.error("CUE Time Parsing Error");
			isValid = false;
			return;
		}
		
		// set ES timestamp by UTC
		esTimestampString = TimeUtil.long2Str(esTimestamp, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",	"GMT+0");
	}


	/**
	 * get ES Index Info
	 * 
	 * @return  ES Index Info
	 */
	public String getLogIndex(){	
		String indexTime = "";
		
		//set ES Index by GMT+9 
		indexTime = TimeUtil.long2Str(esTimestamp, "yyyy.MM.dd", "GMT+9");		
		return LOG_INDEX + indexTime;
	}
}
