package com.skplanet.logsearch.elastic;

import java.util.List;

import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import com.skplanet.logsearch.util.Config;

/** 
* ElasticSearch Interface API Class
*  
* @author JongHwa Lee (SKP)
* @version 0.1 
*   
*/
public class ElasticsearchAPI {
	//static Logger logger = Logger.getLogger(System.getProperty("logger"));
	static Logger logger = Logger.getLogger(ElasticsearchAPI.class);
	static TransportClient client = null;

	static String   cluster;
	static String   host1, host2, host3;
	static String[] esHost;
	static int      esHostNum;
	static int      esPort;
	
	static int    connectedNodes;
	
	
	/**
	 * ElasticsearchAPI Instance
	 * 
	 */	
	public ElasticsearchAPI() {
	
	}
	
	/**
	 * init ElasticsearchAPI Config
	 * 
	 */	
	static public void init() {
		//Load ElasticSearch Configuration Info
		cluster = Config.getString("elasticSearch.cluster");
		
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
		
		esPort = Config.getInt("elasticSearch.port.transport");
		connectedNodes = 0;
	}
	
	/**
	 * start ElasticsearchAPI Client, connect to Elasticsearch Server
	 * 
	 */	
	static public void start() {		
		init();
		
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", cluster).build();		

		client = new TransportClient(settings);

		for (int i=0; i <esHostNum; i++) {
			client = client.addTransportAddress(new InetSocketTransportAddress(esHost[i], esPort));
		}
		
		if (status() == false)
			logger.error("Failed to connect ALL ElasticSearch Server!!!!!");
		
		return;
	}
	
	/**
	 * stop ElasticsearchAPI Client, disconnect to Elasticsearch Server
	 * 
	 */	
	static public void stop() {	
		client.close();
		
		if (status() == true)
			logger.error("Failed to disconnect ALL ElasticSearch Server!!!!!");
	}
	
	/**
	 * stop ElasticsearchAPI Client, disconnect to Elasticsearch Server
	 * 
	 */	
	static public boolean status() {	
		ImmutableList<DiscoveryNode> nodes = client.connectedNodes();
		
		connectedNodes = nodes.size();
		if (connectedNodes == 0) {			
			return false;
		}
		
		for(int i=0; i <connectedNodes; i++)
			logger.info("Connected ElasticSearch Server :" + nodes.get(i).toString());
		
		return true;
	}
	
	/**
	 * Insert Elasticsearch Data
	 * 
	 * @param _index ES index info  
	 * @param _type  ES type info 
	 * @param _json  ES Json date(Json format)
	 */	
	static public void insert(String _index, String _type, String _json)	{	
		try {
			client.prepareIndex(_index, _type)
					.setSource(_json)
					.execute()
					.actionGet();			
		} catch (Exception e) {
			logger.error("ElasticSearch Insert Error [" +_index + "," + _type +"] : "+ e.getMessage());
			logger.error("[json]" + _json);
		} 
	}	
	
	/**
	 * Insert Elasticsearch Data
	 * 
	 * @param _index ES index info  
	 * @param _type  ES type info 
	 * @param _json  ES Json array (Json format)
	 */	
	static public void insertBulk(String _index, String _type, String _json[])	{	
		int countJson = _json.length;
		
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
				
			for(int i=0; i<countJson; i++) {
				bulkRequest.add(client.prepareIndex(_index, _type).setSource(_json[i]));
			}
			
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
			    // process failures by iterating through each bulk response item
				throw new RuntimeException("Failed to index data needed for test. " + bulkResponse.buildFailureMessage());
			}
		} catch (Exception e) {
			logger.error("ElasticSearch Insert Error [" +_index + "," + _type +"] : "+ e.getMessage());
		} 
	}

	/**
	 * Insert Elasticsearch Data
	 * 
	 * @param _index ES index info  
	 * @param _type  ES type info 
	 * @param _json  ES Json array (Json format)
	 */	
	static public void insertBulk(String _index, String _type, List <String> jsonList)	{	
		
		
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
				
			for(String json:jsonList) {
				bulkRequest.add(client.prepareIndex(_index, _type).setSource(json));
			}
			
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
			    // process failures by iterating through each bulk response item
				throw new RuntimeException("Failed to index data needed for test. " + bulkResponse.buildFailureMessage());
			}
		} catch (Exception e) {
			logger.error("ElasticSearch Insert Error [" +_index + "," + _type +"] : "+ e.getMessage());
		} 
	}

	
	/**
	 * Insert(update) Elasticsearch Data by ID
	 * 
	 * @param _index ES index info  
	 * @param _type  ES type info 
	 * @param _id    ES id info
	 * @param _json  ES Json date(Json format)
	 */	
	static public void insertById(String _index, String _type, String _id, String _json) {
		try {
			client.prepareIndex(_index, _type, _id)
					.setSource(_json)
					.execute()
					.actionGet();
		} catch (Exception e) {	
			logger.error("ElasticSearch Insert Error [" +_index + "," + _type + "," + _id + "] : "+ e.getMessage());
			logger.error("[json]" + _json);
		} 
	}
	
	static public void insertBulkById(String _index, String _type, String _id[], String _json[])	{	
		int countJson = _json.length;
		
		try {
			BulkRequestBuilder bulkRequest = client.prepareBulk();
				
			for(int i=0; i<countJson; i++) {
				bulkRequest.add(client.prepareIndex(_index, _type, _id[i]).setSource(_json[i]));
			}
			
			BulkResponse bulkResponse = bulkRequest.execute().actionGet();
			if (bulkResponse.hasFailures()) {
			    // process failures by iterating through each bulk response item
				throw new RuntimeException("Failed to index data needed for test. " + bulkResponse.buildFailureMessage());
			}
		} catch (Exception e) {
			logger.error("ElasticSearch Insert Error [" +_index + "," + _type +"] : "+ e.getMessage());
		} 
	}
	
    /**
     * Create an index without pushing the mapping
     * @param index Index name
     *
     */
    public static boolean createIndex(String index) {
    	boolean flag = true;
    	
        try {
            // We check first if index already exists
            if (!isIndexExist(index)) {
                CreateIndexRequestBuilder cirb = client.admin().indices().prepareCreate(index);
                CreateIndexResponse createIndexResponse = cirb.execute().actionGet();
                if (!createIndexResponse.isAcknowledged()) throw new Exception("Could not create index ["+index+"].");
                logger.info("Index[" + index +"] is created.");
            } else {
                logger.info("Index[" + index +"] already exists.");   
                flag = false;
            }
        } catch (Exception e) {
            logger.error("createIndexIfNeeded() : Exception raised :" + e.getMessage());
            flag = false;
        }
        
        return flag;
    }
    
	/**
	 * Check if an index already exists
	 * @param index Index name
	 * @return true if index already exists
	 * @throws Exception
	 */
    public static boolean isIndexExist(String index) throws Exception {
		return client.admin().indices().prepareExists(index).execute().actionGet().isExists();
	}
    
	/**
	 * Check if a type already exists
	 * @param client Elasticsearch client
	 * @param index Index name
	 * @param type Type name
	 * @return true if index already exists
	 * @throws Exception
	 */
    public static boolean isTypeExist(String index, String type) throws Exception {
		return client.admin().indices().prepareExists(index, type).execute().actionGet().isExists();
	}
}
