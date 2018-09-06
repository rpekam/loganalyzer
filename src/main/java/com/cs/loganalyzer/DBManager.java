package com.cs.loganalyzer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hsqldb.persist.HsqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBManager {

	private static final Logger logger = LoggerFactory.getLogger(DBManager.class);
	static org.hsqldb.server.Server sonicServer;

	public static void startDBServer() {

		String path = DBManager.class.getClassLoader().getResource(File.separator).getPath();
		logger.info("DB Path :" + path);

		HsqlProperties props = new HsqlProperties();
		props.setProperty("server.database.0", "file:" + path + "csdb;");
		props.setProperty("server.dbname.0", "csdb");
		sonicServer = new org.hsqldb.Server();
		try {
			sonicServer.setProperties(props);
		} catch (Exception e) {
			return;
		}
		sonicServer.start();
		// logger.info("DB server started...");
	}

	public static void stopDBServer() {
		if (sonicServer != null) {
			sonicServer.shutdown();
			// logger.info("DB server stoped...");
		}
	}
	
	public static Connection getDBConn() {
		Connection connection = null;
		while (connection == null) {
			try {
				Class.forName("org.hsqldb.jdbcDriver");
				logger.info("DB driver loaded ...");
				connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/csdb", "SA","");
			} catch (SQLException | ClassNotFoundException e) {
				logger.error("Connecting failed, retrying...", e);
			}
		}

		return connection;
	}

}