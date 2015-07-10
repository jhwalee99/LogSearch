package com.skplanet.logsearch.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	static private Properties props = new Properties();
	
	static public void loadFile (String fileName) throws IOException {		
    	FileInputStream fis = new FileInputStream(fileName);
        props.load(new java.io.BufferedInputStream(fis));
	}
	
	static public void setString(String key, String value)
	{
		props.setProperty(key, value);
	}
	
	static public String getString(String key)
	{
		String value;
		value = props.getProperty(key);
		return value;
	}
	
	static public int getInt(String key)
	{
		int value;
		value = Integer.parseInt(props.getProperty(key));
		return value;
	}
	
	static public boolean getBoolean(String key)
	{
		boolean value;
		value = Boolean.parseBoolean(props.getProperty(key));
		
		return value;
	}
}
