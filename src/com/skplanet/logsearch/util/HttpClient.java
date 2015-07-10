package com.skplanet.logsearch.util;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;

public class HttpClient {
	static Logger logger = Logger.getLogger(System.getProperty("logger"));
	private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public static String Get(String desiredUrl, int timeout) throws Exception {

		URL url;
		String contents = null;
		int responseCode = 0;
		StringBuffer responseData = new StringBuffer();
		
		HttpURLConnection connection = null;

		String nurl = desiredUrl;
		
		try {
			url = new URL(nurl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("GET");
			responseCode = connection.getResponseCode();
			connection.connect();
		} catch (Exception e) {
			logger.info("Http Connection Error : " + e.getMessage());
		}
		
		BufferedReader reader = null;
		try {
			if (responseCode == 200) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"UTF-8"));
				String _ResData = null;
				while ((_ResData = reader.readLine()) != null) {
					responseData.append(_ResData);
				}
				contents = new String (responseData.toString().getBytes(), DEFAULT_CHARSET);
			} else if (responseCode != 0){
				logger.info("Response Error : code = " + responseCode);
			}
		} catch (Exception e) {
			logger.info("Http Contents Error : " + e.getMessage());
		} finally {
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.
			if (reader != null) {
				try {
					connection.disconnect();
					reader.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		
		return contents;
	}
}