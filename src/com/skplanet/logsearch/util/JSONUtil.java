package com.skplanet.logsearch.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/** 
* Android JSON Parser 
*  
* @author jaewoong.jeong
* @version 0.1 
*   
*/
public class JSONUtil {
	static  Logger logger = Logger.getLogger(System.getProperty("logger"));
	/**
	 * parses the JSONObject and makes a structured java item recursively.
	 * Field name of item must be matched JSON key because it uses Java reflection.
	 * 
	 * The sample source illustrates how to use JSONParser in your Android applications.
	 * 
	 * @param item root object 
	 * @param jObj json object
	 * 
	 */
	public static void parseElement(Object item, JSONObject jObj) {		
		Iterator<?> keys = jObj.keySet().iterator();
	
		//Iterator<?> keys = jObj.keys();
		while (keys.hasNext()) {  
            String key = (String)keys.next();
            try {
	            Object value = jObj.get(key);
	            
	            if(value instanceof String) {
	            	Field f = item.getClass().getField(key);
	            	f.set(item, value);
	            } else if(value instanceof Integer) {
	            	Field f = item.getClass().getField(key);
	            	f.setInt(item, (Integer)value);
	            } else if(value instanceof Long) {
	            	Field f = item.getClass().getField(key);
	            	f.setLong(item, (Long)value);
	            } else if(value instanceof Double) {
	            	Field f = item.getClass().getField(key);
	            	f.setDouble(item, (Double)value);
	            } else if(value instanceof JSONObject) {
	            	Field f = item.getClass().getField(key);
	            	Object obj = f.getType().newInstance();
	            	f.set(item, obj);
	            	parseElement(obj, (JSONObject)jObj.get(key));	            	
	            } else if(value instanceof JSONArray) {					
					Field f = item.getClass().getField(key);
					ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
					Type actualType[] = parameterizedType.getActualTypeArguments();
					Class<?> c = (Class<?>)actualType[0];
					
					int length = ((JSONArray)value).size();
					//int length = ((JSONArray)value).length();
					ArrayList<Object> array = new ArrayList<Object>(length);
					for(int i = 0; i < length; i++) {
						try {
							Object obj = (Object)c.newInstance();
							parseElement(obj, (JSONObject)((JSONArray)value).get(i));
							array.add(obj);
						} catch(Exception e) {
			            	// skip this elements.
			            }
					}
					f.set(item, array);
	            }           
			} catch (NoSuchFieldException e) {
				// skip this elements.
			} catch (IllegalArgumentException e) {
				// skip this elements.
			} catch (IllegalAccessException e) {
				// skip this elements.
			} catch (InstantiationException e) {
				// skip this elements.
			} catch (Exception e) {
				// skip this elements.
			}
		}
	}
	
	public static void copyStringArray(Object item, JSONObject jObj) {		
		Iterator<?> keys = jObj.keySet().iterator();
		
		while (keys.hasNext()) {  
            String key = (String)keys.next();
            try {
	            Object value = jObj.get(key);
	            
	            if(value instanceof String) {
	            	Field f = item.getClass().getField(key);
	            	f.set(item, value);
	            } else if(value instanceof Integer) {
	            	Field f = item.getClass().getField(key);
	            	f.setInt(item, (Integer)value);
	            } else if(value instanceof Double) {
	            	Field f = item.getClass().getField(key);
	            	f.setDouble(item, (Double)value);
	            } else if(value instanceof JSONObject) {
	            	Field f = item.getClass().getField(key);
	            	Object obj = f.getType().newInstance();
	            	f.set(item, obj);
	            	parseElement(obj, (JSONObject)jObj.get(key));
	            } else if(value instanceof JSONArray) {					
					Field f = item.getClass().getField(key);
					ParameterizedType parameterizedType = (ParameterizedType) f.getGenericType();
					Type actualType[] = parameterizedType.getActualTypeArguments();
					Class<?> c = (Class<?>)actualType[0];
					
					int length = ((JSONArray)value).size();
					ArrayList<String> array = new ArrayList<String>(length);
					for(int i = 0; i < length; i++) {
						try {							
							String obj = (String)c.newInstance();
							obj = ((JSONArray)value).get(i).toString();
							array.add(obj);
						} catch(Exception e) {
			            	// skip this elements.
			            }
					}
					f.set(item, array);
	            }
			} catch (NoSuchFieldException e) {
				// skip this elements.
			} catch (IllegalArgumentException e) {
				// skip this elements.
			} catch (IllegalAccessException e) {
				// skip this elements.
			} catch (InstantiationException e) {
				// skip this elements.
			} catch (Exception e) {
				// skip this elements.
			}
		}
	}
}