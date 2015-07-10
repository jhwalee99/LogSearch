package com.skplanet.logsearch.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class Counter {
	static Logger logger;
	int           maxCount;
	AtomicInteger registerToggle;
	AtomicInteger lastIdx;
	AtomicLong    hourCount[];
	AtomicLong    dayCount[];
	AtomicLong    oldHourCount[];
	AtomicLong    oldDayCount[];
	String        titles[];
	String        catagoryName;
	String        currentTime;
	ConcurrentHashMap<String, Integer>   indexMap;
	
	public Counter(String name, int size) {
		//logger Info
		String loggerName = "counter.logger";
		if (System.getProperty(loggerName) == null) {
			loggerName = "logger";
		}		
		logger = Logger.getLogger(System.getProperty(loggerName));
		
		//counter items Info
		maxCount  = size;
		lastIdx = new AtomicInteger();
		lastIdx.set(0);
		registerToggle = new AtomicInteger();
		registerToggle.set(0);
		
		titles       = new String[maxCount];
		hourCount    = new AtomicLong[maxCount];
		dayCount     = new AtomicLong[maxCount];
		oldHourCount = new AtomicLong[maxCount];
		oldDayCount  = new AtomicLong[maxCount];
		
		//catagory name
		catagoryName = name;
		
		indexMap = new ConcurrentHashMap<String, Integer>();
	}
	
	public int register(String title) {
		int     current = -1;
		Integer index;
		
		//////////////////////////////////////////////////
		//�ߺ� ���� : �ð� ���� sleep�� ����Ͽ� �ߺ� ����� ������
		//////////////////////////////////////////////////
		sleeping();		
		index = indexMap.get(title);
		if (index != null) {
			return index.intValue();
		}
		////////////////////////////////////////////////
		
		current = lastIdx.getAndIncrement();
		if (current == maxCount) {
			logger.error("SHOULD change counters size (counters.size :  " + maxCount + ")");
			System.exit(0);
		}
		
		titles[current]       = title;
		hourCount[current]    = new AtomicLong();
		dayCount[current]     = new AtomicLong();
		oldHourCount[current] = new AtomicLong();
		oldDayCount[current]  = new AtomicLong();
		
		hourCount[current].set(0);
		dayCount[current].set(0);
		oldHourCount[current].set(0);
		oldDayCount[current].set(0);		

		index = current;
		indexMap.put(title, index);
		
		return current;
	}
	
	/**
	 * counter list�� index ��ȸ
	 *
	 * @param title counter title  
	 */	
	public int getIndex(String title) {
		Integer index;
		 
		index = indexMap.get(title);
		
		if (index == null) {
			index = register(title);			
		}
		
		return index.intValue();
	}
	
	/**
	 * ������  index�� counter (+1) ����
	 * 
	 * @param index counter list�� index
	 */	
	public void inc(int index) {
		if(index >= maxCount) return;
		dayCount[index].incrementAndGet();
		hourCount[index].incrementAndGet();
	}
	
	/**
	 * ������  index�� counter (count) ����
	 * 
	 * @param index counter list�� index
	 */	
	public void inc(int index, int count) {
		if(index >= maxCount) return;
		dayCount[index].addAndGet(count);
		hourCount[index].addAndGet(count);
	}
	
	
	/**
	 * ������  index�� counter (-1) ����
	 * 
	 * @param index counter list�� index
	 */	
	public void dec(int index) {
		if(index >= maxCount) return;
		dayCount[index].decrementAndGet();
		hourCount[index].decrementAndGet();
	}
	
	/**
	 * ������  index�� counter ������ display��
	 * 
	 * @param index counter list�� index
	 */
	public void display(int index) {	
		logger.info(titles[index] + " : "  + dayCount[index] + "/" + hourCount[index]);
	}
	
	/**
	 * hourly, daily count��  display�ϱ� ���� string�� 
	 * 
	 * @return display string
	 */
	public StringBuilder display() {
		int last = lastIdx.intValue();
		StringBuilder lines = new StringBuilder();
		String line = "";
				
		line = String.format("   %-20s \n", catagoryName);
		lines.append(line);
		long tempCount;
		
		for (int i=0; i < last; i++) {
			tempCount =  hourCount[i].get() + dayCount[i].get() + oldHourCount[i].get() + oldDayCount[i].get();
			
			if (tempCount != 0) {
				line = String.format("        %-25s : [%10d / %10d] [%10d / %10d]\n", titles[i], hourCount[i].get(), dayCount[i].get(), oldHourCount[i].get(), oldDayCount[i].get());
				lines.append(line);
			}
		}
		
		return lines;
	}
	
	/**
	 * current count�� display�ϱ� ���� string �� 
	 * 
	 * @return display string
	 */
	public StringBuilder displayCurrent() {
		int last = lastIdx.intValue();
		StringBuilder lines = new StringBuilder();
		String line = "";
				
		line = String.format("   %-20s \n", catagoryName);
		lines.append(line);
		
		for (int i=0; i < last; i++) {
			line = String.format("        %-25s : %10d\n", titles[i], hourCount[i].get());
			lines.append(line);
		}
		
		return lines;
	}
	
	/**
	 * counter item�� hourly, daily reset
	 * 
	 * @param hour reset�Ǵ� �ð�(0 ~ 23)
	 */
	public void reset(int hour) {
		int last = lastIdx.intValue();
		
		for (int i=0; i < last; i++) {
			oldHourCount[i].set(hourCount[i].get());
			hourCount[i].set(0);
			if (hour == 0) {
				oldDayCount[i].set(dayCount[i].get()); 
				dayCount[i].set(0);
			}
		}
	}
	
	/**
	 * set current time  
	 * 
	 */
	public void setCurrentTime(String current) {		
		currentTime = current;
	}
	
	/**
	 * ���� Key�� �ߺ� �ԷµǴ� ���� ���� �ϱ� ����  sleeping 
	 * 
	 */
	private void sleeping() {
		int interval;
		int toggle = registerToggle.getAndIncrement();
		
		interval = toggle * 10;
		if ((toggle % 5) == 0) registerToggle.set(0);
		
    	try {
        	Thread.sleep(interval);
    	} catch (InterruptedException ie) {

    	}
	}
}