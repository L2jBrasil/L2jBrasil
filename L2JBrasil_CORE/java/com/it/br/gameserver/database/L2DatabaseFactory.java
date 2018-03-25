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
package com.it.br.gameserver.database;

import com.it.br.Config;
import com.it.br.configuration.settings.DatabaseSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static com.it.br.configuration.Configurator.getSettings;

public class L2DatabaseFactory {
    private static Logger _log = LoggerFactory.getLogger(L2DatabaseFactory.class);

    private static L2DatabaseFactory _instance;
    private final HikariDataSource _dataSource;

    public L2DatabaseFactory() throws SQLException {

        DatabaseSettings databaseSettings = getSettings(DatabaseSettings.class);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(databaseSettings.getUrl());
        config.setUsername(databaseSettings.getUsername());
        config.setPassword(databaseSettings.getPassword());
        config.addDataSourceProperty("cachePrepStmts", databaseSettings.isPreparedStatementCacheEnabled());
        config.addDataSourceProperty("prepStmtCacheSize", databaseSettings.getPreparedStatementCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", databaseSettings.getLimitSqlCache());
        config.addDataSourceProperty("useServerPrepStmts", databaseSettings.isServerPreparedStatementEnabled());
        config.addDataSourceProperty("useLocalSessionState", databaseSettings.isLocalSessionStateEnabled());
        config.addDataSourceProperty("useLocalTransactionState", databaseSettings.isLocalTransactionStateEnabled());
        config.addDataSourceProperty("rewriteBatchedStatements", databaseSettings.isRewriteBatchedStatementEnabled());
        config.addDataSourceProperty("cacheServerConfiguration", databaseSettings.isServerCacheEnabled());
        config.addDataSourceProperty("cacheResultSetMetadata", databaseSettings.isResultsetCacheEnabled());
        config.addDataSourceProperty("maintainTimeStats", databaseSettings.isMaintainTimestatsEnabled());
        config.addDataSourceProperty("logger", "com.mysql.jdbc.log.Slf4JLogger");

        _dataSource = new HikariDataSource(config);
        _dataSource.setAutoCommit(databaseSettings.isAutoCommitEnabled());
        _dataSource.setMinimumIdle(databaseSettings.getMinIdle());

        _dataSource.setValidationTimeout(databaseSettings.getValidationTimeout()); // 500 milliseconds wait before try to acquire connection again
        _dataSource.setConnectionTimeout(databaseSettings.getConnectionTimeout()); // 0 = wait indefinitely for new connection if pool is exhausted
        _dataSource.setMaximumPoolSize(databaseSettings.getMaxConnections());
        _dataSource.setIdleTimeout(databaseSettings.getConnectionIdleTimeout()); // 0 = idle connections never expire
        _dataSource.setDriverClassName(databaseSettings.getDriver());


        // Test DB connection
        try {
            _dataSource.getConnection().close();
        } catch (SQLException e) {
            if (Config.DEBUG) {
                _log.error("Database Connection FAILED");
            }
            _log.error(e.getMessage(), e);
        }
    }

    public final static String prepQuerySelect(String[] fields, String tableName, String whereClause, boolean returnOnlyTopRecord) {
        String msSqlTop1 = "";
        String mySqlTop1 = "";
        if (returnOnlyTopRecord) {
            mySqlTop1 = " Limit 1 ";
        }
        String query = "SELECT " + msSqlTop1 + safetyString(fields) + " FROM " + tableName + " WHERE " + whereClause + mySqlTop1;
        return query;
    }

    public void shutdown() {
        try {
            _dataSource.close();
        } catch (Exception e) {
            _log.info(e.getMessage(), e);
        }

    }

    public final static String safetyString(String[] whatToCheck) {
        // NOTE: Use brace as a safty percaution just incase name is a reserved word
        String braceLeft = "`";
        String braceRight = "`";

        String result = "";
        for (String word : whatToCheck) {
            if (result != "") result += ", ";
            result += braceLeft + word + braceRight;
        }
        return result;
    }

    public static L2DatabaseFactory getInstance() throws SQLException {
        if (_instance == null) {
            _instance = new L2DatabaseFactory();
        }
        return _instance;
    }

    public Connection getConnection() //throws SQLException
    {
        Connection con = null;

        while (con == null) {
            try {
                con = _dataSource.getConnection();
            } catch (SQLException e) {
                _log.warn("L2DatabaseFactory: getConnection() failed, trying again", e);
            }
        }
        return con;
    }

    public static void close(Connection conn) {
    }
}
