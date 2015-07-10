package com.skplanet.logsearch.util;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

import org.apache.log4j.Logger;


public class HttpsClient implements X509TrustManager {
	static Logger logger = Logger.getLogger(System.getProperty("logger"));
	private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	static {		
		try {
			SSLContext sc;
			sc = SSLContext.getInstance("SSLv3");
			TrustManager[] tma = { new HttpsClient() };
			sc.init(null, tma, null);
			SSLSocketFactory ssf = sc.getSocketFactory();
			HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
			HostnameVerifier hv = new HostnameVerifier() {
				public boolean verify(String urlHostName, SSLSession session) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {			
			logger.info("PRTG Http SSL Configuration Error : " + e.getMessage());
		}
	}
	
	public static String Get(String desiredUrl, int timeout) throws Exception {

		URL url;
		String contents = null;
		int responseCode = 0;
		StringBuffer responseData = new StringBuffer();
		
		HttpsURLConnection connection = null;

		String nurl = desiredUrl;
		
		try {
			url = new URL(nurl);
			connection = (HttpsURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setReadTimeout(timeout);
			connection.setRequestMethod("GET");
			responseCode = connection.getResponseCode();
			connection.connect();
		} catch (Exception e) {
			logger.info("PRTG Https Connection Error : " + e.getMessage());
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
				logger.info("PRTG Response Error : code = " + responseCode);
			}
		} catch (Exception e) {
			logger.info("PRTG Https Contents Error : " + e.getMessage());
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

	// TrustManager Methods
	public void checkClientTrusted(X509Certificate[] chain, String authType) {
	}

	public void checkServerTrusted(X509Certificate[] chain, String authType) {
	}

	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}