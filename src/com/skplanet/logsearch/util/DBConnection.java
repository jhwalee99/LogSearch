package com.skplanet.logsearch.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DBConnection {
	static  Logger  logger = Logger.getLogger(System.getProperty("logger"));
	
	static  private  String dbDriver;
	static  private  String userId;
	static  private  String passwd;

	private  Connection conn = null;
	
	static  public void set(String _dbDriver, String _userId, String _passwd) {
		dbDriver = _dbDriver;
		userId   = _userId;
		passwd   = _passwd;
	}
		
	public Connection getConnection(){
		
		try {
			if (conn == null) {
				// The newInstance() call is a work around for some
				// broken Java implementations
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				conn = DriverManager.getConnection(dbDriver, userId, passwd);
			}

			if (conn.isValid(5) == false) {
				conn.close();				
				Thread.sleep(3000);
				conn = DriverManager.getConnection(dbDriver, userId, passwd);
			}
		} catch (SQLException ex) {
			logger.error("DBConnection Error(" + ex.getErrorCode() + ":"+ ex.getSQLState() + ") " + ex.getMessage());
		} catch (Exception ex) {
			// handle the error
		}
					
		return conn;
	}
}
