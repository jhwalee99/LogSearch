package com.skplanet.logsearch.jobs;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {
	static private Scheduler scheduler;
	
	static public void start() {    	
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    	
	}
	
	@SuppressWarnings("rawtypes")
	static public void addJob(String jobName, Class jobClass, String triggerName, String cronPattern) {
    	JobDetail job = new JobDetail();
    	job.setName(jobName);
    	job.setJobClass(jobClass);
    	    	
    	CronTrigger trigger = new CronTrigger();
    	trigger.setName(triggerName);
    	    	
    	try {
    		trigger.setCronExpression(cronPattern);
    		scheduler.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}    	
	}
}
