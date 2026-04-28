package service;

import model.AppUser;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AuthService {

    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : b) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static AppUser login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, role, active FROM app_user " +
                     "WHERE username = ? AND password_hash = ? AND active = TRUE";
        try (PreparedStatement ps = DBConnection.get().prepareStatement(sql)) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, hash(password));
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return null;
            return new AppUser(rs.getInt("user_id"), rs.getString("username"),
                               rs.getString("role"), rs.getBoolean("active"));
        }
    }

    public static void createUser(String username, String password, String role)
            throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "INSERT INTO app_user(username, password_hash, role) VALUES (?,?,?)")) {
            ps.setString(1, username.trim().toLowerCase());
            ps.setString(2, hash(password));
            ps.setString(3, role);
            ps.executeUpdate();
        }
    }

    public static void toggleUser(int userId, boolean active) throws SQLException {
        try (PreparedStatement ps = DBConnection.get().prepareStatement(
                "UPDATE app_user SET active = ? WHERE user_id = ?")) {
            ps.setBoolean(1, active);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}
