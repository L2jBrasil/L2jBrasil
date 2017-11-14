package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class DatabaseSettings implements Settings {
	
	private String driver;
	private String url;
	private String username;
	private String password;
	private int maxConnections;
	private int connectionIdleTimeout;
	private boolean enabledServerCache;
	private boolean enabledPreparedStatementCache;
	private int PreparedStatementCacheSize;
	private int limitSqlCache;
	private boolean enabledResultsetCache;
	private boolean enabledServerPreparedStatement;
	private boolean enabledLocalSessionState;
	private boolean enableLocalTransactionState;
	private boolean enabledRewriteBatchedStatement;
	private boolean enabledMaintainTimestats;
	private boolean enabledAutoCommit;
	private int minIdle;
	private int validationTimeout;
	private int connectionTimeout;
	
	@Override
	public void load(L2Properties properties) {
		driver = properties.getString("Driver", "com.mysql.jdbc.Driver");
		url = properties.getString("Url", "jdbc:mysql://localhost/l2jdb");
		username = properties.getString("Username", "root");
		password = properties.getString("Password", "root");
		maxConnections = properties.getInteger("MaxConnection", 10);
		
		if(maxConnections < 2) {
			maxConnections = 2;
		}
		
		connectionIdleTimeout = properties.getInteger("ConnectionIdleTimeout", 0);
		
		enabledServerCache = properties.getBoolean("AllowServerCache", true);
		enabledPreparedStatementCache = properties.getBoolean("AllowPreparedStatementCache", true);
		
		PreparedStatementCacheSize = properties.getInteger("PreparedStatementCacheSize", 250);
		limitSqlCache = properties.getInteger("PreparedStatementCacheSqlLimit", 2048);
		enabledResultsetCache = properties.getBoolean("AllowResultsetCache", true);
		enabledServerPreparedStatement = properties.getBoolean("AllowPreparedStatement", true);
		
		enabledLocalSessionState = properties.getBoolean("AllowLocalSessionState", true);
		enableLocalTransactionState = properties.getBoolean("AllowLocalTransaction", true);

		enabledRewriteBatchedStatement = properties.getBoolean("AllowRewriteBatchedStatement", true);
		enabledMaintainTimestats = properties.getBoolean("AllowMaintainTimestats", true);
		enabledAutoCommit = properties.getBoolean("AllowAutoCommit", true);
		
		minIdle = properties.getInteger("ConnectionMinIdle", 10);
		validationTimeout = properties.getInteger("ConnectionValidationTimeout", 500);
		connectionTimeout = properties.getInteger("ConnectionTimeout", 0);
	}
	
	public String getDriver() {
		return driver;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public int getMaxConnections() {
		return maxConnections;
	}
	
	public void setMaxConnections(int count) {
		this.maxConnections = count;
	}
	
	public int getConnectionIdleTimeout() {
		return connectionIdleTimeout;
	}
	
	public boolean isEnabledServerCache() {
		return enabledServerCache;
	}
	
	public int getPreparedStatementCacheSize() {
		return PreparedStatementCacheSize;
	}
	
	public int getLimitSqlCache() {
		return limitSqlCache;
	}
	
	public boolean isEnabledResultsetCache() {
		return enabledResultsetCache;
	}
	
	public boolean isEnabledPreparedStatementCache() {
		return enabledPreparedStatementCache;
	}
	
	public boolean isEnabledServerPreparedStatement() {
		return enabledServerPreparedStatement;
	}
	
	public boolean isEnabledLocalSessionState() {
		return enabledLocalSessionState;
	}
	
	public boolean isEnabledLocalTransactionState() {
		return enableLocalTransactionState;
	}
	
	public boolean isEnabledRewriteBatchedStatement() {
		return enabledRewriteBatchedStatement;
	}
	
	public boolean isEnabledMaintainTimestats() {
		return enabledMaintainTimestats;
	}
	
	public boolean isEnabledAutoCommit() {
		return enabledAutoCommit;
	}
	
	public int getMinIdle() {
		return minIdle;
	}
	
	public int getValidationTimeout() {
		return validationTimeout;
	}
	
	public int getConnectionTimeout() {
		return connectionTimeout;
	}
}
