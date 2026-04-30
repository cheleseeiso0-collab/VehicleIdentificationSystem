package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.PoliceReport;
import com.example.vehicleidentificationsystem.model.Violation;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class PoliceController {

    @FXML private TableView<PoliceReport> reportTable;
    @FXML private TextField repVehicleId, repDate, repDesc, repOfficer;
    @FXML private ComboBox<String> repTypeBox;

    @FXML private TableView<Violation> violationTable;
    @FXML private TextField violVehicleId, violDate, violFine;
    @FXML private ComboBox<String> violTypeBox, violStatusBox;

    @FXML
    public void initialize() {
        setupReportTable();
        setupViolationTable();
        populateComboBoxes();
        loadReports();
        loadViolations();

        reportTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                repVehicleId.setText(String.valueOf(sel.getVehicleId()));
                repDate.setText(sel.getReportDate());
                repTypeBox.setValue(sel.getReportType());
                repDesc.setText(sel.getDescription());
                repOfficer.setText(sel.getOfficerName());
            }
        });

        violationTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                violVehicleId.setText(String.valueOf(sel.getVehicleId()));
                violDate.setText(sel.getViolationDate());
                violTypeBox.setValue(sel.getViolationType());
                violFine.setText(String.valueOf(sel.getFineAmount()));
                violStatusBox.setValue(sel.getStatus());
            }
        });

        ValidationHelper.applyNameFilter(repOfficer);
        ValidationHelper.applyDateFilter(repDate);
        ValidationHelper.applyDateFilter(violDate);
    }

    private void setupReportTable() {
        reportTable.getColumns().clear();
        reportTable.getColumns().addAll(
                repCol("ID", "reportId", 50),
                repCol("Vehicle ID", "vehicleId", 80),
                repCol("Date", "reportDate", 100),
                repCol("Type", "reportType", 100),
                repCol("Description", "description", 200),
                repCol("Officer", "officerName", 150)
        );
    }

    private void setupViolationTable() {
        violationTable.getColumns().clear();
        violationTable.getColumns().addAll(
                violCol("ID", "violationId", 50),
                violCol("Vehicle ID", "vehicleId", 80),
                violCol("Date", "violationDate", 100),
                violCol("Type", "violationType", 120),
                violCol("Fine (M)", "fineAmount", 80),
                violCol("Status", "status", 80)
        );
    }

    private void populateComboBoxes() {
        repTypeBox.setItems(FXCollections.observableArrayList(
                "Accident", "Theft", "Roadblock", "Licence Check", "Other"));
        violTypeBox.setItems(FXCollections.observableArrayList(
                "Speeding", "Running Red Light", "No Seatbelt", "No Licence",
                "Drunk Driving", "Illegal Parking", "Other"));
        violStatusBox.setItems(FXCollections.observableArrayList("Unpaid", "Paid"));
    }

    @FXML private void handleAddReport() {
        try {
            String dateStr = repDate.getText().trim();
            LocalDate repDateParsed = Date.valueOf(dateStr).toLocalDate();
            if (repDateParsed.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Report date cannot be in the future.");
                return;
            }
            String sql = "INSERT INTO police_reports (vehicle_id, report_date, report_type, description, officer_name) VALUES (?,?,?,?,?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(repVehicleId.getText().trim()));
                pst.setDate(2, Date.valueOf(dateStr));
                pst.setString(3, repTypeBox.getValue());
                pst.setString(4, repDesc.getText().trim());
                pst.setString(5, repOfficer.getText().trim());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Report added.");
                clearReportFields();
                loadReports();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Add failed: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateReport() {
        PoliceReport sel = reportTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a report."); return; }
        try {
            String dateStr = repDate.getText().trim();
            LocalDate repDateParsed = Date.valueOf(dateStr).toLocalDate();
            if (repDateParsed.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Report date cannot be in the future.");
                return;
            }
            String sql = "UPDATE police_reports SET vehicle_id=?, report_date=?, report_type=?, description=?, officer_name=? WHERE report_id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(repVehicleId.getText().trim()));
                pst.setDate(2, Date.valueOf(dateStr));
                pst.setString(3, repTypeBox.getValue());
                pst.setString(4, repDesc.getText().trim());
                pst.setString(5, repOfficer.getText().trim());
                pst.setInt(6, sel.getReportId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Report updated.");
                clearReportFields();
                loadReports();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteReport() {
        PoliceReport sel = reportTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a report."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete this report?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM police_reports WHERE report_id=?")) {
            pst.setInt(1, sel.getReportId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Report removed.");
            clearReportFields();
            loadReports();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleAddViolation() {
        try {
            String dateStr = violDate.getText().trim();
            LocalDate violDateParsed = Date.valueOf(dateStr).toLocalDate();
            if (violDateParsed.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Violation date cannot be in the future.");
                return;
            }
            String sql = "INSERT INTO violations (vehicle_id, violation_date, violation_type, fine_amount, status) VALUES (?,?,?,?,?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(violVehicleId.getText().trim()));
                pst.setDate(2, Date.valueOf(dateStr));
                pst.setString(3, violTypeBox.getValue());
                pst.setDouble(4, Double.parseDouble(violFine.getText().trim()));
                pst.setString(5, violStatusBox.getValue());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Violation added.");
                clearViolationFields();
                loadViolations();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Add failed: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateViolation() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a violation."); return; }
        try {
            String dateStr = violDate.getText().trim();
            LocalDate violDateParsed = Date.valueOf(dateStr).toLocalDate();
            if (violDateParsed.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Violation date cannot be in the future.");
                return;
            }
            String sql = "UPDATE violations SET vehicle_id=?, violation_date=?, violation_type=?, fine_amount=?, status=? WHERE violation_id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, Integer.parseInt(violVehicleId.getText().trim()));
                pst.setDate(2, Date.valueOf(dateStr));
                pst.setString(3, violTypeBox.getValue());
                pst.setDouble(4, Double.parseDouble(violFine.getText().trim()));
                pst.setString(5, violStatusBox.getValue());
                pst.setInt(6, sel.getViolationId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Violation updated.");
                clearViolationFields();
                loadViolations();
            }
        } catch (Exception e) {
            AlertHelper.showError("Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteViolation() {
        Violation sel = violationTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a violation."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete this violation?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM violations WHERE violation_id=?")) {
            pst.setInt(1, sel.getViolationId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Violation removed.");
            clearViolationFields();
            loadViolations();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleCancelReport()   { clearReportFields(); }
    @FXML private void handleCancelViolation() { clearViolationFields(); }

    private void loadReports() {
        ObservableList<PoliceReport> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM police_reports ORDER BY report_date DESC")) {
            while (rs.next()) {
                list.add(new PoliceReport(
                        rs.getInt("report_id"), rs.getInt("vehicle_id"),
                        rs.getString("report_date"), rs.getString("report_type"),
                        rs.getString("description"), rs.getString("officer_name")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load reports: " + e.getMessage());
        }
        reportTable.setItems(list);
    }

    private void loadViolations() {
        ObservableList<Violation> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM violations ORDER BY violation_date DESC")) {
            while (rs.next()) {
                list.add(new Violation(
                        rs.getInt("violation_id"), rs.getInt("vehicle_id"),
                        rs.getString("violation_date"), rs.getString("violation_type"),
                        rs.getDouble("fine_amount"), rs.getString("status")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load violations: " + e.getMessage());
        }
        violationTable.setItems(list);
    }

    private void clearReportFields() {
        repVehicleId.clear(); repDate.clear(); repTypeBox.setValue(null);
        repDesc.clear(); repOfficer.clear();
    }

    private void clearViolationFields() {
        violVehicleId.clear(); violDate.clear(); violTypeBox.setValue(null);
        violFine.clear(); violStatusBox.setValue(null);
    }

    private <T> TableColumn<PoliceReport, T> repCol(String title, String prop, int width) {
        TableColumn<PoliceReport, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }

    private <T> TableColumn<Violation, T> violCol(String title, String prop, int width) {
        TableColumn<Violation, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }
}