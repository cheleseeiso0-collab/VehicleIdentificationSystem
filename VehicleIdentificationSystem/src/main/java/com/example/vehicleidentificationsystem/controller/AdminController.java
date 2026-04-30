package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.SystemUser;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.SessionManager;
import com.example.vehicleidentificationsystem.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.Optional;

public class AdminController {

    @FXML private TableView<SystemUser> userTable;
    @FXML private TextField newUsername;
    @FXML private PasswordField newPassword;
    @FXML private TextField newFullName;
    @FXML private ComboBox<String> newRoleBox;

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isAdmin()) {
            VBox root = (VBox) userTable.getParent();
            root.getChildren().clear();
            root.getChildren().add(new Label("Access Denied"));
            return;
        }

        setupTable();
        populateRoles();
        loadUsers();

        ValidationHelper.applyNameFilter(newFullName);
    }

    private void setupTable() {
        userTable.getColumns().clear();
        userTable.getColumns().addAll(
                col("ID", "userId", 50),
                col("Username", "username", 120),
                col("Full Name", "fullName", 180),
                col("Role", "role", 100),
                colActive("Active", "active", 80)
        );
    }

    private void populateRoles() {
        newRoleBox.setItems(FXCollections.observableArrayList(
                "ADMIN", "WORKSHOP", "CUSTOMER", "POLICE", "INSURANCE"));
        newRoleBox.setValue("WORKSHOP");
    }

    @FXML private void handleCreateUser() {
        String username = newUsername.getText().trim();
        String password = newPassword.getText();
        String fullName = newFullName.getText().trim();
        String role = newRoleBox.getValue();
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || role == null) {
            AlertHelper.showWarning("Validation", "All fields are required.");
            return;
        }
        String sql = "INSERT INTO system_users (username, password, role, full_name, active) VALUES (?,?,?,?,TRUE)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            pst.setString(4, fullName);
            pst.executeUpdate();
            AlertHelper.showInfo("Success", "User created.");
            clearCreateForm();
            loadUsers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Creation failed: " + e.getMessage());
        }
    }

    @FXML private void handleToggleActive() {
        SystemUser sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a user."); return; }
        String sql = "UPDATE system_users SET active = NOT active WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, sel.getUserId());
            pst.executeUpdate();
            AlertHelper.showInfo("Toggled", "User status changed.");
            loadUsers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Toggle failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteUser() {
        SystemUser sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a user."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete user " + sel.getUsername() + "?")) return;
        String sql = "DELETE FROM system_users WHERE user_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, sel.getUserId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "User removed.");
            loadUsers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleEditUser() {
        SystemUser sel = userTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a user to edit."); return; }

        Dialog<SystemUser> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user: " + sel.getUsername());
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setPadding(new javafx.geometry.Insets(20));

        TextField usernameField = new TextField(sel.getUsername());
        PasswordField passwordField = new PasswordField();
        TextField fullNameField = new TextField(sel.getFullName());
        ValidationHelper.applyNameFilter(fullNameField);

        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.setItems(FXCollections.observableArrayList("ADMIN","WORKSHOP","CUSTOMER","POLICE","INSURANCE"));
        roleCombo.setValue(sel.getRole());
        CheckBox activeCheck = new CheckBox("Active");
        activeCheck.setSelected(sel.isActive());

        grid.add(new Label("Username:"), 0, 0); grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1); grid.add(passwordField, 1, 1);
        grid.add(new Label("Full Name:"), 0, 2); grid.add(fullNameField, 1, 2);
        grid.add(new Label("Role:"), 0, 3); grid.add(roleCombo, 1, 3);
        grid.add(activeCheck, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String newUsername = usernameField.getText().trim();
                String newFullName = fullNameField.getText().trim();
                if (newUsername.isEmpty() || newFullName.isEmpty()) {
                    AlertHelper.showWarning("Validation", "Username and Full Name cannot be empty.");
                    return null;
                }
                return new SystemUser(sel.getUserId(), newUsername, passwordField.getText(),
                        roleCombo.getValue(), newFullName, activeCheck.isSelected());
            }
            return null;
        });

        Optional<SystemUser> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            String password = updated.getPassword();
            String sql;
            if (password.isEmpty()) {
                sql = "UPDATE system_users SET username=?, role=?, full_name=?, active=? WHERE user_id=?";
            } else {
                sql = "UPDATE system_users SET username=?, password=?, role=?, full_name=?, active=? WHERE user_id=?";
            }
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, updated.getUsername());
                if (password.isEmpty()) {
                    pst.setString(2, updated.getRole());
                    pst.setString(3, updated.getFullName());
                    pst.setBoolean(4, updated.isActive());
                    pst.setInt(5, updated.getUserId());
                } else {
                    pst.setString(2, password);
                    pst.setString(3, updated.getRole());
                    pst.setString(4, updated.getFullName());
                    pst.setBoolean(5, updated.isActive());
                    pst.setInt(6, updated.getUserId());
                }
                pst.executeUpdate();
                AlertHelper.showInfo("Updated", "User details saved.");
                loadUsers();
            } catch (SQLException e) {
                AlertHelper.showError("Error", "Update failed: " + e.getMessage());
            }
        });
    }

    @FXML private void handleCancel() {
        clearCreateForm();
    }

    private void loadUsers() {
        ObservableList<SystemUser> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM system_users ORDER BY user_id")) {
            while (rs.next()) {
                list.add(new SystemUser(
                        rs.getInt("user_id"), rs.getString("username"),
                        "", rs.getString("role"),
                        rs.getString("full_name"), rs.getBoolean("active")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load users: " + e.getMessage());
        }
        userTable.setItems(list);
    }

    private void clearCreateForm() {
        newUsername.clear();
        newPassword.clear();
        newFullName.clear();
        newRoleBox.setValue("WORKSHOP");
    }

    private <T> TableColumn<SystemUser, T> col(String title, String prop, int width) {
        TableColumn<SystemUser, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }

    private TableColumn<SystemUser, Boolean> colActive(String title, String prop, int width) {
        TableColumn<SystemUser, Boolean> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        c.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item ? "Yes" : "No");
            }
        });
        return c;
    }
}
