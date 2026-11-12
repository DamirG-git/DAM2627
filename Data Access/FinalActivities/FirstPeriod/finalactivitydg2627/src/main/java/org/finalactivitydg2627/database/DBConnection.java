package org.finalactivitydg2627.database;

import org.finalactivitydg2627.Utils.ApplicationLogger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final ApplicationLogger logger = ApplicationLogger.getInstance();
    private static final Properties properties = new Properties();
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    private static class DatabaseException extends RuntimeException {
        public DatabaseException(String message, IOException e) {
            super(message+e.getMessage(), e);
        }
    }

    static {
        try(var input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if(input == null) {
                logger.log("Properties file not found!", ApplicationLogger.LogLevel.ERROR);
                throw new DatabaseException("Properties file not found!",null);
            }
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            logger.log("Could not load database configuration: ", ApplicationLogger.LogLevel.ERROR);
            throw new DatabaseException("Could not load database configuration: ",e);
        }
    }

    private static final DBConnection instance = new DBConnection();

    private  DBConnection() {}
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DBConnection getInstance() {
        return instance;
    }




}
