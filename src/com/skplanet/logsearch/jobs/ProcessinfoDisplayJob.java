package com.skplanet.logsearch.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.skplanet.logsearch.util.ProcessInfo;

public class ProcessinfoDisplayJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ProcessInfo.display();
	}
}