package com.kms.challenges.rbh.dao;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author tkhuu.
 */
public class ConnectionManager {
    private static BoneCP connectionPool;
    static {
        // default to get configuratyion from ./config/database.properties file
        Properties properties = new Properties();
        try {
            properties.load(ConnectionManager.class.getResourceAsStream("/database.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Can't not read database properties",e);
        }
        String connectionString = properties.getProperty("connectionString");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        BoneCPConfig config = new BoneCPConfig();
        config.setJdbcUrl(connectionString);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinConnectionsPerPartition(5); //if you say 5 here, there will be 10 connection available   config.setMaxConnectionsPerPartition(10);
        config.setPartitionCount(2);
        try {
            connectionPool = new BoneCP(config);
            initDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Can't not initialize connection pool", e);
        }
    }
    public static BoneCP getConnectionPool() {
        return connectionPool;
    }
    public static void shutdown(){
        connectionPool.shutdown();
    }
    public static Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }
    public static void initDatabase() throws SQLException {
        for (String statement : getInitStatements()) {
            try (Connection connection = ConnectionManager.getConnection(); Statement statementSql =connection.createStatement()){
                statementSql.execute(statement);
            }
        }
    }

    public static List<String> getInitStatements() {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(ConnectionManager.class.getResourceAsStream("/init-database.sql")));
        String line = "";
        String statement = "";
        List<String> statementList = new ArrayList<>();
        try {
            while ((line = reader.readLine()) != null) {
                if (line.endsWith(";")) {
                    statementList.add(statement + line);
                    statement = "";
                    continue;
                }
                if (!line.isEmpty()) {
                    statement = statement + line;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Can't get init database statements", e);
        }
        return statementList;
    }
}
