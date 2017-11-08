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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class L2DatabaseFactory
{
    static Logger _log = Logger.getLogger(L2DatabaseFactory.class.getName());

    private static L2DatabaseFactory _instance;
	private final HikariDataSource _dataSource;

	public L2DatabaseFactory() throws SQLException
	{
		if (Config.DATABASE_MAX_CONNECTIONS < 2)
        {
            Config.DATABASE_MAX_CONNECTIONS = 2;
            _log.warning("A minimum of 2 connections are required.");
        }

        // Hello Hikari!
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.DATABASE_URL);
        config.setUsername(Config.DATABASE_LOGIN);
        config.setPassword(Config.DATABASE_PASSWORD);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        _dataSource = new HikariDataSource(config);
        _dataSource.setAutoCommit(true);
        _dataSource.setMinimumIdle(10);

        _dataSource.setValidationTimeout(500); // 500 milliseconds wait before try to acquire connection again
        _dataSource.setConnectionTimeout(0); // 0 = wait indefinitely for new connection if pool is exhausted
        _dataSource.setMaximumPoolSize(Config.DATABASE_MAX_CONNECTIONS);
        _dataSource.setIdleTimeout(Config.DATABASE_MAX_IDLE_TIME); // 0 = idle connections never expire
        _dataSource.setDriverClassName(Config.DATABASE_DRIVER);

        // Test DB connection
        try
        {
            _dataSource.getConnection().close();
		} catch (SQLException e)
		{
			if (Config.DEBUG) {
			    _log.fine("Database Connection FAILED");
			}

            e.printStackTrace();

            // FIXME: New exception inside an exception catching block?
			//throw new SQLException("could not init DB connection:"+e);
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
