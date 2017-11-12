/*
 * This program is free software; you can redistribute it and/or modify
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
package com.it.br;

import static com.it.br.configuration.Configurator.getSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.it.br.configuration.settings.DatabaseSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class L2DatabaseFactory
{
    static Logger _log = Logger.getLogger(L2DatabaseFactory.class.getName());

    private static L2DatabaseFactory _instance;
	private final HikariDataSource _dataSource;

	public L2DatabaseFactory() throws SQLException
	{
		
		DatabaseSettings databaseSettings = getSettings(DatabaseSettings.class);
	
        // Hello Hikari!
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseSettings.getUrl());
        config.setUsername(databaseSettings.getUsername());
        config.setPassword(databaseSettings.getPassword());
        config.addDataSourceProperty("cachePrepStmts", databaseSettings.isEnablePreparedStatementCache());
        config.addDataSourceProperty("prepStmtCacheSize", databaseSettings.getPreparedStatementCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", databaseSettings.getLimitSqlCache());
        config.addDataSourceProperty("useServerPrepStmts", databaseSettings.isEnableServerPreparedStatement());
        config.addDataSourceProperty("useLocalSessionState", databaseSettings.isEnableLocalSessionState());
        config.addDataSourceProperty("useLocalTransactionState", databaseSettings.isEnableLocalTransactionState());
        config.addDataSourceProperty("rewriteBatchedStatements", databaseSettings.isEnableRewriteBatchedStatement());
        config.addDataSourceProperty("cacheServerConfiguration", databaseSettings.isEnableServerCache());
        config.addDataSourceProperty("cacheResultSetMetadata", databaseSettings.isEnableResultsetCache());
        config.addDataSourceProperty("maintainTimeStats", databaseSettings.isEnableMaintainTimestats());

        _dataSource = new HikariDataSource(config);
        _dataSource.setAutoCommit(databaseSettings.isEnableAutoCommit());
        _dataSource.setMinimumIdle(databaseSettings.getMinIdle());

        _dataSource.setValidationTimeout(databaseSettings.getValidationTimeout()); // 500 milliseconds wait before try to acquire connection again
        _dataSource.setConnectionTimeout(databaseSettings.getConnectionTimeout()); // 0 = wait indefinitely for new connection if pool is exhausted
        _dataSource.setMaximumPoolSize(databaseSettings.getMaxConnections());
        _dataSource.setIdleTimeout(databaseSettings.getConnectionIdleTimeout()); // 0 = idle connections never expire
        _dataSource.setDriverClassName(databaseSettings.getDriver());
        
        
        // Test DB connection
        try
        {
            _dataSource.getConnection().close();
		} catch (SQLException e)
		{
			if (Config.DEBUG) {
			    _log.fine("Database Connection FAILED");
			}
			_log.severe(e.getMessage());
		}
	}

	public final String prepQuerySelect(String[] fields, String tableName, String whereClause, boolean returnOnlyTopRecord)
	{
		String msSqlTop1 = "";
		String mySqlTop1 = "";
		if (returnOnlyTopRecord)
		{
			mySqlTop1 = " Limit 1 ";
		}
		String query = "SELECT " + msSqlTop1 + safetyString(fields) + " FROM " + tableName + " WHERE " + whereClause + mySqlTop1;
		return query;
	}

    public void shutdown()
    {
        try
        {
			_dataSource.close();
        }
        catch
        (Exception e) {_log.log(Level.INFO, "", e);}

    }
    public final String safetyString(String[] whatToCheck)
    {
        // NOTE: Use brace as a safty percaution just incase name is a reserved word
        String braceLeft = "`";
        String braceRight = "`";

        String result = "";
        for(String word : whatToCheck)
        {
            if(result != "") result += ", ";
            result += braceLeft + word + braceRight;
        }
        return result;
    }
	public static L2DatabaseFactory getInstance() throws SQLException
	{
		if (_instance == null)
		{
			_instance = new L2DatabaseFactory();
		}
		return _instance;
	}
	public Connection getConnection() //throws SQLException
	{
		Connection con=null;

		while(con==null)
		{
			try
			{
				con= _dataSource.getConnection();
			}catch (SQLException e)
			{
				_log.warning("L2DatabaseFactory: getConnection() failed, trying again "+e);
			}
		}
		return con;
	}

	public static void close(Connection conn){}
}
