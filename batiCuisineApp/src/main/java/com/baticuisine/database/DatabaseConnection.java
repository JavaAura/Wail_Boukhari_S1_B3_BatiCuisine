package com.baticuisine.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.baticuisine.utils.PropertyLoader;

public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static final String DB_URL = PropertyLoader.getProperty("db.url");
    private static final String USER = PropertyLoader.getProperty("db.username");
    private static final String PASSWORD = PropertyLoader.getProperty("db.password");

    private static volatile DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("ssl", "false");

            this.connection = DriverManager.getConnection(DB_URL, props);
            LOGGER.info("Database connection established successfully.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to establish database connection", e);
            throw new RuntimeException("Failed to connect to the database", e);
        }
    }

    public static DatabaseConnection getInstance() {
        DatabaseConnection instance = DatabaseConnection.instance;
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                instance = DatabaseConnection.instance;
                if (instance == null) {
                    DatabaseConnection.instance = instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void closeConnection() {
        try (Connection conn = this.connection) {
            if (conn != null && !conn.isClosed()) {
                LOGGER.info("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to close database connection", e);
        }
    }
}