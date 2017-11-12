/* This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.it.br.configuration.settings;

import com.it.br.configuration.L2Properties;

/**
 *
 * @author  Alisson Oliveira
 */
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
		driver = properties.getString("driver", "com.mysql.jdbc.Driver");
		url = properties.getString("url", "jdbc:mysql://localhost/l2jdb");
		username = properties.getString("username", "root");
		password = properties.getString("password", "root");
		maxConnections = properties.getInteger("connection.max", 10);
		
		if(maxConnections < 2) {
			maxConnections = 2;
		}
		
		connectionIdleTimeout = properties.getInteger("connection.idle.timeout", 0);
		
		enabledServerCache = properties.getBoolean("allow.cache.server", true);
		enabledPreparedStatementCache = properties.getBoolean("prepared.statement.cache.allow", true);
		
		PreparedStatementCacheSize = properties.getInteger("prepared.statement.cache.size", 250);
		limitSqlCache = properties.getInteger("prepared.statement.cache.sql.limit", 2048);
		enabledResultsetCache = properties.getBoolean("allow.cache.resultset", true);
		enabledServerPreparedStatement = properties.getBoolean("allow.server.prepared.statement", true);
		
		enabledLocalSessionState = properties.getBoolean("allow.local.session.state", true);
		enableLocalTransactionState = properties.getBoolean("allow.local.transaction", true);

		enabledRewriteBatchedStatement = properties.getBoolean("allow.rewrite.batched.statement", true);
		enabledMaintainTimestats = properties.getBoolean("allow.maintain.timestats", true);
		enabledAutoCommit = properties.getBoolean("allow.auto.commit", true);
		
		minIdle = properties.getInteger("connection.idle.min", 10);
		validationTimeout = properties.getInteger("connection.validation.timeout", 500);
		connectionTimeout = properties.getInteger("connection.timeout", 0);
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
