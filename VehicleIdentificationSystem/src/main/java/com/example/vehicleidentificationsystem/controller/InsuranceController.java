package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.InsurancePolicy;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class InsuranceController {

    @FXML private TableView<InsurancePolicy> policyTable;
    @FXML private TextField vehIdField, providerField, policyNumField;
    @FXML private TextField startDateField, expiryDateField, coverageField, premiumField;

    @FXML
    public void initialize() {
        setupTable();
        loadPolicies();

        policyTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                vehIdField.setText(String.valueOf(sel.getVehicleId()));
                providerField.setText(sel.getProvider());
                policyNumField.setText(sel.getPolicyNumber());
                startDateField.setText(sel.getStartDate());
                expiryDateField.setText(sel.getExpiryDate());
                coverageField.setText(sel.getCoverageType());
                premiumField.setText(String.valueOf(sel.getPremium()));
            }
        });

        ValidationHelper.applyNameFilter(providerField);
        ValidationHelper.applyDateFilter(startDateField);
        ValidationHelper.applyDateFilter(expiryDateField);

        ValidationHelper.applyIntegerFilter(vehIdField);
        ValidationHelper.applyIntegerFilter(premiumField);
    }

    private void setupTable() {
        policyTable.getColumns().clear();
        policyTable.getColumns().addAll(
                col("ID", "policyId", 40),
                col("Vehicle ID", "vehicleId", 80),
                col("Provider", "provider", 150),
                col("Policy #", "policyNumber", 100),
                col("Start", "startDate", 90),
                col("Expiry", "expiryDate", 90),
                col("Coverage", "coverageType", 110),
                col("Premium", "premium", 80)
        );
    }

    private void loadPolicies() {
        ObservableList<InsurancePolicy> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM insurance_policies ORDER BY policy_id")) {
            while (rs.next()) {
                list.add(new InsurancePolicy(
                        rs.getInt("policy_id"), rs.getInt("vehicle_id"),
                        rs.getString("provider"), rs.getString("policy_number"),
                        rs.getString("start_date"), rs.getString("expiry_date"),
                        rs.getString("coverage_type"), rs.getDouble("premium")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load policies: " + e.getMessage());
        }
        policyTable.setItems(list);
    }

    @FXML private void handleAddPolicy() {
        try {
            LocalDate startDate = Date.valueOf(startDateField.getText().trim()).toLocalDate();
            if (startDate.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Start date cannot be in the future.");
                return;
            }
            String sql = "INSERT INTO insurance_policies (vehicle_id, provider, policy_number, start_date, expiry_date, coverage_type, premium) VALUES (?,?,?,?,?,?,?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(vehIdField.getText().trim()));
                pst.setString(2, providerField.getText().trim());
                pst.setString(3, policyNumField.getText().trim());
                pst.setDate(4, Date.valueOf(startDateField.getText().trim()));
                pst.setDate(5, Date.valueOf(expiryDateField.getText().trim()));
                pst.setString(6, coverageField.getText().trim());
                pst.setDouble(7, Double.parseDouble(premiumField.getText().trim()));
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Policy added.");
                clearForm();
                loadPolicies();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Add failed: " + e.getMessage());
        }
    }

    @FXML private void handleUpdatePolicy() {
        InsurancePolicy sel = policyTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a policy."); return; }
        try {
            LocalDate startDate = Date.valueOf(startDateField.getText().trim()).toLocalDate();
            if (startDate.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Start date cannot be in the future.");
                return;
            }
            String sql = "UPDATE insurance_policies SET vehicle_id=?, provider=?, policy_number=?, start_date=?, expiry_date=?, coverage_type=?, premium=? WHERE policy_id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(vehIdField.getText().trim()));
                pst.setString(2, providerField.getText().trim());
                pst.setString(3, policyNumField.getText().trim());
                pst.setDate(4, Date.valueOf(startDateField.getText().trim()));
                pst.setDate(5, Date.valueOf(expiryDateField.getText().trim()));
                pst.setString(6, coverageField.getText().trim());
                pst.setDouble(7, Double.parseDouble(premiumField.getText().trim()));
                pst.setInt(8, sel.getPolicyId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Policy updated.");
                clearForm();
                loadPolicies();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeletePolicy() {
        InsurancePolicy sel = policyTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a policy."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete this policy?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM insurance_policies WHERE policy_id=?")) {
            pst.setInt(1, sel.getPolicyId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Policy removed.");
            clearForm();
            loadPolicies();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleCancelPolicy() { clearForm(); }

    private void clearForm() {
        vehIdField.clear(); providerField.clear(); policyNumField.clear();
        startDateField.clear(); expiryDateField.clear();
        coverageField.clear(); premiumField.clear();
    }

    private <T> TableColumn<InsurancePolicy, T> col(String title, String prop, int width) {
        TableColumn<InsurancePolicy, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        c.setPrefWidth(width);
        return c;
    }
}