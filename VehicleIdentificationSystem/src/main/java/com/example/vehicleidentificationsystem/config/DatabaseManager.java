package com.example.vehicleidentificationsystem.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/vehicle_db";
    private static final String USER   = "postgres";
    private static final String PASS   = "123456";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void callAddVehicle(String regNo, String make, String model,
                                      int year, int ownerId) throws SQLException {
        String sql = "{call add_vehicle(?,?,?,?,?)}";
        try (Connection conn = getConnection();
             java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, regNo);
            stmt.setString(2, make);
            stmt.setString(3, model);
            stmt.setInt   (4, year);
            stmt.setInt   (5, ownerId);
            stmt.execute();
        }
    }

    public static void callDeleteVehicle(int vehicleId) throws SQLException {
        String sql = "{call delete_vehicle(?)}";
        try (Connection conn = getConnection();
             java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.execute();
        }
    }

    public static void callUpdateVehicle(int vehicleId, String regNo, String make,
                                         String model, int year, int ownerId) throws SQLException {
        String sql = "{call update_vehicle(?,?,?,?,?,?)}";
        try (Connection conn = getConnection();
             java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt   (1, vehicleId);
            stmt.setString(2, regNo);
            stmt.setString(3, make);
            stmt.setString(4, model);
            stmt.setInt   (5, year);
            stmt.setInt   (6, ownerId);
            stmt.execute();
        }
    }

    public static void callAddCustomer(String name, String address,
                                       String phone, String email) throws SQLException {
        String sql = "{call add_customer(?,?,?,?)}";
        try (Connection conn = getConnection();
             java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, address);
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.execute();
        }
    }

    public static void callUpdateCustomer(int customerId, String name, String address,
                                          String phone, String email) throws SQLException {
        String sql = "{call update_customer(?,?,?,?,?)}";
        try (Connection conn = getConnection();
             java.sql.CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt   (1, customerId);
            stmt.setString(2, name);
            stmt.setString(3, address);
            stmt.setString(4, phone);
            stmt.setString(5, email);
            stmt.execute();
        }
    }
}