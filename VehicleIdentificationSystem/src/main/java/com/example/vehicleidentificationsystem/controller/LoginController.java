package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.SystemUser;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.*;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;
    @FXML private Button loginButton;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();
        if (user.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Username and password required.");
            return;
        }
        String sql = "SELECT user_id, username, password, role, full_name, active " +
                "FROM system_users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, user);
            pst.setString(2, pass);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                if (!rs.getBoolean("active")) {
                    statusLabel.setText("Account is disabled.");
                    return;
                }
                SystemUser current = new SystemUser(
                        rs.getInt("user_id"), rs.getString("username"),
                        rs.getString("password"), rs.getString("role"),
                        rs.getString("full_name"), rs.getBoolean("active")
                );
                SessionManager.getInstance().setCurrentUser(current);

                Stage stage = (Stage) loginButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/com/example/vehicleidentificationsystem/Dashboard.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root, 1200, 720));
                stage.setTitle("Vehicle Identification System – " + current.getFullName());
                stage.setMaximized(true);   // ← dashboard opens full‑screen
                stage.setResizable(true);
            } else {
                statusLabel.setText("Invalid credentials.");
            }
        } catch (Exception e) {
            statusLabel.setText("Database error.");
            e.printStackTrace();
        }
    }
}