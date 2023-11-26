package org.mcq.database;

import org.mcq.dao.ExamDao;
import org.mcq.dao.ExamHistoryDao;
import redis.clients.jedis.Jedis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionFactory {
    public static Connection createDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:database/database.db");
    }

    public static Jedis createRedisConnection() {
        return new Jedis("localhost", 6379);
    }

    public static void closeConnections(Connection connection, Jedis jedis) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        if (jedis != null && jedis.isConnected()) {
            jedis.close();
        }
    }
}

