package com.skplanet.logsearch.kafka;

import com.skplanet.logsearch.log.*;
import com.skplanet.logsearch.util.Config;
import com.skplanet.logsearch.util.Counter;
import com.skplanet.logsearch.util.CounterGroup;
import com.skplanet.logsearch.util.ProcessInfo;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.log4j.Logger;

/** 
* Kafka Consumer Worker(Runnable)
*  
* @author JongHwa Lee (SKP)
* @version 0.1 
*   
*/
public class KafkaWorker implements Runnable {
	static  Logger logger = Logger.getLogger(System.getProperty("logger"));
	static private ConsumerConnector consumer;
    static private ExecutorService   executor;
           
    static private int      counterIndex;
    static private int      totalIndex; 
    
	static private Counter  kafkaCounter;
	
	static private boolean  isRunning;

	/**
	 * Instance method
	 */
    public KafkaWorker() {
        
    }
     
	/**
	 * execute(start) Kafka Consumer Worker thread
	 */
	public static void start() 
	{
    	try {
    		ConsumerConfig config = createConsumerConfig();
    		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(config);
		} catch (IOException e) {
			logger.error("EXIT <main>  IOException : " + e.getMessage());
			System.exit(0);
		} catch (ZkTimeoutException e) {
			logger.error("EXIT <main> InterruptedException : " + e.getMessage());
			System.exit(0);
		}
    	//runtimeException e �߰� ...
		    	
    	int numThread = 1;
		executor = Executors.newFixedThreadPool(numThread);
		
		for(int i=0; i<numThread; i++) 
		{		
			executor.execute(new KafkaWorker());
		}
		
		//Kafka counter index
		kafkaCounter = CounterGroup.register("Kafka", 16);
		counterIndex = kafkaCounter.register("Receive");	
		totalIndex   = ProcessInfo.register("Kafka-Total");
		
		isRunning = true;
	}
	
	/**
	 * stop Kafka Consumer Worker thread
	 */	
    public static void shutdown() {
    	isRunning = false;
    	
		try {
    		Thread.sleep(1000);
    	} catch(InterruptedException ex) {
        }
    	
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
    }
 
	/**
	 *  Kafka Consumer Worker run
	 */
	@Override
	public void run() {		        
	    String topic = Config.getString("kafka.topic");
		
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
		
		KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);

		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		
		String kafkaMsg = "";

		//receive kafka message
		while (it.hasNext()) {
			byte[] messages = it.next().message();
			
			if(messages == null) continue; 
			if(messages.length == 0) continue;
			
			kafkaMsg = new String(messages);
			
			//increase Kafka counter
			kafkaCounter.inc(counterIndex);
			
			//increase Kafka total counter
			ProcessInfo.inc(totalIndex);
			
			LogWorker.put(kafkaMsg);			
			
			if (isRunning == false) break;
		}    
		
		logger.error("Kafka Consumer Worker is shutdowned.");
    }
	
	/**
	 *  Create Kafka Consumer Config
	 *  
	 *  @return Kafka Consumer Config 
	 */
    static private ConsumerConfig createConsumerConfig() throws IOException {
    	//Load Kafka Config Info
    	Properties props = new Properties();    	
    	props.put("zookeeper.connect",       Config.getString("kafka.zookeeper.connect"));
    	props.put("group.id",                Config.getString("kafka.group.id"));
    	props.put("auto.commit.interval.ms", Config.getString("kafka.auto.commit.interval.ms"));
    	props.put("auto.commit.enable",      Config.getString("kafka.auto.commit.enable"));
    	props.put("auto.offset.reset",       Config.getString("kafka.auto.offset.reset"));
    	props.put("consumer.timeout.ms",     Config.getString("kafka.consumer.timeout.ms"));
    	
        return new ConsumerConfig(props);        
    }
}
