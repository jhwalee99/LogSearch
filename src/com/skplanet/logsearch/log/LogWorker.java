package com.skplanet.logsearch.log;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/** 
* Log Handle Worker(Runnable)
*  
* @author JongHwa Lee (SKP)	
* @version 0.1 
*   
*/
public class LogWorker implements Runnable 
{	
	static  Logger  logger = Logger.getLogger(System.getProperty("logger"));
	
	private static ConcurrentLinkedQueue<String> logQueue = new ConcurrentLinkedQueue<String>();
	private static ExecutorService pool;
	private static boolean isRunning = false;
	
	private LogBulk logBulk;
	private LogStreamManager streamManager;
	
	
	/**
	 * Instance method
	 */
	public LogWorker() {
		logBulk = new LogBulk();
		streamManager = new LogStreamManager();
	}
	
	/**
	 * execute(start) Log worker thread
	 */
	public static void start() {
		isRunning = true;
		int numThread = 1;
		
		pool = Executors.newFixedThreadPool(numThread);
		for(int i=0; i<numThread; i++) {
			pool.execute(new LogWorker());
		}
	}
	
	/**
	 * stop Log worker thread
	 */	
	public static void stop() {
		isRunning = false;
		synchronized (logQueue) {
			logQueue.notifyAll();
		}
		
		pool.shutdownNow();
	}
	
	/**
	 * insert Log to LogQueue
	 * 	 
	 * @param messages Log(Json format)  
	 */	
	public static void put(String kafkaMsg) {	
		logQueue.add(kafkaMsg);
		
		synchronized (logQueue) {
			logQueue.notifyAll();
		}
	}
	
	public static int getQueueSize() {
		return logQueue.size();
	}
		
	/**
	 * Log Worker run
	 */
	@Override
	public void run() {
		while (isRunning) {
			if (logQueue.size() > 0) {
				processLog();
			} else {
				idle();
				processIdle();
			} 
		}
	}
	
	public void processLog() {
		String kafkaMsg = logQueue.poll();
		if (kafkaMsg == null) return;
		
		String logStr = streamManager.processLog(kafkaMsg);
		if (logStr == null) return;
		
		LogObject logObject = new LogObject(logStr);
		if (logObject.isValid == false) return;
		
		logBulk.processLog(logObject);
	}

	public void idle() {
		synchronized (logQueue) {
			try {
				logQueue.wait(10);
			} catch (InterruptedException e) {
			}
		}
	}
	
	public void processIdle() {
		List<String> timeoutList = streamManager.processIdle();
		
		for (String log:timeoutList) {
			LogObject logObject = new LogObject(log);
			if (logObject.isValid == false) continue;
		
			logBulk.processLog(logObject);
		}
		
		logBulk.processIdle();
	}
}