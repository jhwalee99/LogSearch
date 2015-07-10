package com.skplanet.logsearch;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.skplanet.logsearch.elastic.ElasticsearchAPI;
import com.skplanet.logsearch.jobs.CounterDisplayJob;
import com.skplanet.logsearch.jobs.JobScheduler;
import com.skplanet.logsearch.jobs.ProcessinfoDisplayJob;
import com.skplanet.logsearch.kafka.KafkaWorker;
import com.skplanet.logsearch.log.LogWorker;
import com.skplanet.logsearch.util.Config;
import com.skplanet.logsearch.util.CounterGroup;
import com.skplanet.logsearch.util.DBConnection;
import com.skplanet.logsearch.util.ProcessInfo;
import com.skplanet.logsearch.util.TimeUtil;

public class LogSearch {
	static  Logger  logger = Logger.getLogger("backend");
	static  boolean cueEnable;
	
	public static void initConfig(String file) {		
        //Set log4j property
		System.setProperty("logger", "backend");
		System.setProperty("counter.logger", "counter");
				
    	//Load Configuration Info
        try {
			Config.loadFile(file);
	        
		} catch (IOException e) {
			logger.error("Fail to load Config file : " + e.getMessage());
			System.exit(0);
		}
        
		//DB Connection Info Set
        String dbDriver = Config.getString("db.driver");
        String dbUser   = Config.getString("db.user");
        String dbPasswd = Config.getString("db.passwd");        
        DBConnection.set(dbDriver, dbUser, dbPasswd );
        
        //count init
        CounterGroup.init("PMON_BACKEND", 32);
        String timeStr = TimeUtil.currentStr("yyyy-MM-dd HH", "GMT+9");
        CounterGroup.setCurrentTime(timeStr);
        
        //processinfo init
        ProcessInfo.init("PMON_BACKEND");
        
        cueEnable  = Config.getBoolean("cue.enable");
	}

	public static void elasticsearchModule() {
		// Start ElasticSearch Worker
		ElasticsearchAPI.start();		
	}
	
	public static void cueModule() {
		if (cueEnable != true) return;
		
		// Start CUE Log Worker
		LogWorker.start();

		// Start Kafka Worker
		KafkaWorker.start();
	}
	
	
	public static void jobModule() {
        //initialize Job Scheduler
        JobScheduler.start();
		JobScheduler.addJob("CounterDisplayJob",     CounterDisplayJob.class,        "CounterDisplayTrigger",     "1 * * * * ?");
		JobScheduler.addJob("ProcessinfoDisplayJob", ProcessinfoDisplayJob.class,    "ProcessinfoDisplayTrigger", "1,2,3,4,5 * * * * ?");
	}
	
	public static void Loop() {
        //Looping
        while (true) {
        	try {
            	Thread.sleep(10000);
        	} catch (InterruptedException ie) {
 
        	}
        }		
	}
	
	public static void addShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
        	public void run() {
        		logger.error("!!!!! Graceful Shutdown Starting !!!!!");
        		
        		KafkaWorker.shutdown();
        		int i = 0;
        		
        		while (i < 3) {        			
        			if (LogWorker.getQueueSize() <= 0) break;

        			try {
            			Thread.sleep(1000);
            		} catch(InterruptedException ex) {
            		}
        			
        			i++;
        		}
        		
        		ProcessInfo.shutdown();
        		CounterGroup.shutdown();        		
        		logger.error("!!!!!!!!!!! Graceful Shutdown  DONE !!!!!!!!!!!!!");
        	}
        });
	}
	
    public static void main(String[] args) {             
        //initialize configuration
        initConfig(args[0]);	
		
        //Elasticsearch Part
        elasticsearchModule();

        //CUE Part
        cueModule();

        //job Part
        jobModule();

        //add shutdown
        addShutdown();
        
        //loop
        Loop();
    }
}
