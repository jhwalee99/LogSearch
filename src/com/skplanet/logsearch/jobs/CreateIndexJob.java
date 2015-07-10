package com.skplanet.logsearch.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;
import com.skplanet.logsearch.util.TimeUtil;

public class CreateIndexJob implements Job {
	static private final String BM_PREFIX      = "bm-";
	static private final String HOST_PREFIX    = "host-";
	static private final String SERVICE_PREFIX = "service-";	
	static private final String SUMMARY_PREFIX = "summary-hourly-";

	static Logger  logger = Logger.getLogger(System.getProperty("logger"));
	static int     sleeping = 0;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		createIndex();
	}
	
	public void createIndex() {
		String index       = "";
		String timePostfix = "";
		boolean flag;
		
		checkSleep();
		
		long   currenTime = System.currentTimeMillis();
		long   indexTime;
		
		indexTime   = TimeUtil.addTime(currenTime, "daily");
		timePostfix = TimeUtil.long2Str(indexTime, "yyyy.MM.dd", "GMT+9");	

		index = BM_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);
		
		index = HOST_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);

		index = SERVICE_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);

		index = BM_PREFIX + SUMMARY_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);
		
		index = HOST_PREFIX + SUMMARY_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);

		index = SERVICE_PREFIX + SUMMARY_PREFIX + timePostfix;
		flag = ElasticsearchAPI.createIndex(index);

		if (flag == true) {
			sleeping = 0;
		} else {
			sleeping = 5;
		}
	}
	
	public void checkSleep() {
        if (sleeping == 0) return;
        
        try {
        	logger.debug("Sleeping.... " + sleeping + " secs");
           	Thread.sleep(sleeping * 1000L);
       	} catch (InterruptedException ie) {}
	}
}