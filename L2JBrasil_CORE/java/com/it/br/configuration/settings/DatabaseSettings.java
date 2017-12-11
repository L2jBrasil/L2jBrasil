package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

public class DatabaseSettings implements Settings {
	
	private String driver;
	private String url;
	private String username;
	private String password;
	private int maxConnections;
	private int connectionIdleTimeout;
	private boolean serverCacheEnabled;
	private boolean preparedStatementCacheEnabled;
	private int PreparedStatementCacheSize;
	private int limitSqlCache;
	private boolean resultsetCacheEnabled;
	private boolean serverPreparedStatementEnabled;
	private boolean localSessionStateEnabled;
	private boolean ocalTransactionStateEnabled;
	private boolean rewriteBatchedStatementEnabled;
	private boolean maintainTimestatsEnabled;
	private boolean autoCommitEnabled;
	private int minIdle;
	private int validationTimeout;
	private int connectionTimeout;
	
	@Override
	public void load(L2Properties properties) {
		if(properties == null) {
			return;
		}
		
		driver = properties.getString("Driver", "com.mysql.jdbc.Driver");
		url = properties.getString("Url", "jdbc:mysql://localhost/l2jdb");
		username = properties.getString("Username", "root");
		password = properties.getString("Password", "root");
		maxConnections = properties.getInteger("MaxConnection", 10);
		
		if(maxConnections < 2) {
			maxConnections = 2;
		}
		
		connectionIdleTimeout = properties.getInteger("ConnectionIdleTimeout", 0);
		
		serverCacheEnabled = properties.getBoolean("AllowServerCache", true);
		preparedStatementCacheEnabled = properties.getBoolean("AllowPreparedStatementCache", true);
		
		PreparedStatementCacheSize = properties.getInteger("PreparedStatementCacheSize", 250);
		limitSqlCache = properties.getInteger("PreparedStatementCacheSqlLimit", 2048);
		resultsetCacheEnabled = properties.getBoolean("AllowResultsetCache", true);
		serverPreparedStatementEnabled = properties.getBoolean("AllowPreparedStatement", true);
		
		localSessionStateEnabled = properties.getBoolean("AllowLocalSessionState", true);
		ocalTransactionStateEnabled = properties.getBoolean("AllowLocalTransaction", true);

		rewriteBatchedStatementEnabled = properties.getBoolean("AllowRewriteBatchedStatement", true);
		maintainTimestatsEnabled = properties.getBoolean("AllowMaintainTimestats", true);
		autoCommitEnabled = properties.getBoolean("AllowAutoCommit", true);
		
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
	
	public boolean isServerCacheEnabled() {
		return serverCacheEnabled;
	}
	
	public int getPreparedStatementCacheSize() {
		return PreparedStatementCacheSize;
	}
	
	public int getLimitSqlCache() {
		return limitSqlCache;
	}
	
	public boolean isResultsetCacheEnabled() {
		return resultsetCacheEnabled;
	}
	
	public boolean isPreparedStatementCacheEnabled() {
		return preparedStatementCacheEnabled;
	}
	
	public boolean isServerPreparedStatementEnabled() {
		return serverPreparedStatementEnabled;
	}
	
	public boolean isLocalSessionStateEnabled() {
		return localSessionStateEnabled;
	}
	
	public boolean isLocalTransactionStateEnabled() {
		return ocalTransactionStateEnabled;
	}
	
	public boolean isRewriteBatchedStatementEnabled() {
		return rewriteBatchedStatementEnabled;
	}
	
	public boolean isMaintainTimestatsEnabled() {
		return maintainTimestatsEnabled;
	}
	
	public boolean isAutoCommitEnabled() {
		return autoCommitEnabled;
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
