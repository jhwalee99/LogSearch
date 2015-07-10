package com.skplanet.logsearch.elastic;


import org.apache.log4j.Logger;

import com.skplanet.logsearch.util.Config;
import com.skplanet.logsearch.util.HttpClient;

/** 
* ElasticSearch Interface API Class
*  
* @author JongHwa Lee (SKP)
* @version 0.1 
*   
*/
public class ElasticsearchREST {
	static Logger logger = Logger.getLogger(System.getProperty("logger"));
    static private final int MAX_ELASTICSEARCH_NUM = 3;
    
	static private String[] restUrl = new String[MAX_ELASTICSEARCH_NUM];
	static private int      timeout;
	static private int      connectedNodes;	
	
	static private String[]  esHost;
	static private int       esHostNum;
	/**
	 * ElasticsearchREST Instance
	 * 
	 */	
	public ElasticsearchREST() {
	
	}
	
	/**
	 * init ElasticsearchREST Config
	 * 
	 */	
	static public void init(String _url, int _timeout) {
		//Load ElasticSearch Configuration Info
		int     port  = Config.getInt("elasticSearch.port.rest");		
		
		restUrl[0] = "http://" + Config.getString("elasticSearch.host.1") + ":" + port + _url;
		restUrl[1] = "http://" + Config.getString("elasticSearch.host.2") + ":" + port + _url;
		restUrl[2] = "http://" + Config.getString("elasticSearch.host.3") + ":" + port + _url;
		
		timeout = _timeout;
		
		connectedNodes = 0;
		
		//TODO SHOULD modify
		/* 
		//host  number
		esHostNum  = Config.getInt("elasticSearch.host.num");
		String hostItem = "";
		
		esHost = new String[esHostNum];
		int index;
		
		for (int i=0; i <esHostNum; i++) {
			index = i+1;
			hostItem = "elasticSearch.host." + index;
			esHost[i] = Config.getString(hostItem);			
		}
		*/
	}
	
	/**
	 * start ElasticsearchREST Client, connect to Elasticsearch Server
	 * 
	 */	
	static public String getHttp(String param) {
		String response = "";
		String url      = "";
		int    i        = 0;
		
		while (i < MAX_ELASTICSEARCH_NUM) {
			url = restUrl[connectedNodes] + "?"+param;
			
			logger.debug(url);
			
			try {
				response = HttpClient.Get(url, timeout);
				if ((response == null) || (response.length() ==0)) {
					change();
					i++;					
					continue;
				}
				break;
			} catch (Exception e) {
				logger.error("Elasticsearch-REST HTTP(s) GET Error: [" + e.getMessage() + "] " + url);
				change();
				i++;
			}
		}
		
		return response;
	}
	
	/**
	 * change  Elasticsearch Server
	 * 
	 */	
	static public void change() {	
		connectedNodes++;
		if (connectedNodes == MAX_ELASTICSEARCH_NUM) connectedNodes = 0;
	}
}
