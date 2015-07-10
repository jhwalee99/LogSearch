package com.skplanet.logsearch.jobs;

import java.util.Calendar;
import java.util.Locale;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.skplanet.logsearch.util.CounterGroup;
import com.skplanet.logsearch.util.TimeUtil;

public class CounterDisplayJob implements Job {
	public void execute(JobExecutionContext context) throws JobExecutionException {
		CounterGroup.display();
		
		Calendar cal = Calendar.getInstance(Locale.KOREA);		
		int minute = cal.get(Calendar.MINUTE);
		
		if (minute == 0) {
			CounterGroup.reset(cal.get(Calendar.HOUR_OF_DAY));
			
		    String timeStr = TimeUtil.currentStr("yyyy-MM-dd HH", "GMT+9");
		    CounterGroup.setCurrentTime(timeStr);			
		}
	}
}