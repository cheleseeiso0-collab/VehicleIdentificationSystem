package service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static Connection connection;

    private DBConnection() {}

    public static Connection get() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = new Properties();
                try (InputStream in = DBConnection.class
                        .getClassLoader().getResourceAsStream("db.properties")) {
                    if (in == null)
                        throw new SQLException("db.properties not found on classpath.");
                    props.load(in);
                } catch (IOException e) {
                    throw new SQLException("Cannot read db.properties: " + e.getMessage(), e);
                }
                connection = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
                );
            }
            return connection;
        } catch (SQLException e) {
            throw new SQLException("Database connection failed: " + e.getMessage(), e);
        }
    }

    public static void close() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException ignored) {}
    }
}
