package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.Customer;
import com.example.vehicleidentificationsystem.model.ServiceRecord;
import com.example.vehicleidentificationsystem.model.Vehicle;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.Year;

public class VehicleController {

    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TextField regField, makeField, modelField, yearField;
    @FXML private TextField editRegField, editMakeField, editModelField, editYearField;

    // Owner dropdowns
    @FXML private ComboBox<Customer> ownerCombo;
    @FXML private ComboBox<Customer> editOwnerCombo;

    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TextField sVehicleIdField, sDateField, sTypeField, sDescField, sCostField;
    @FXML private TextField editServiceVehicleId, editServiceDate, editServiceType, editServiceDesc, editServiceCost;

    @FXML
    public void initialize() {
        setupVehicleTable();
        setupServiceTable();
        loadAllVehicles();
        loadAllServices();
        loadOwnerComboBoxes();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editRegField.setText(sel.getRegistrationNumber());
                editMakeField.setText(sel.getMake());
                editModelField.setText(sel.getModel());
                editYearField.setText(String.valueOf(sel.getYear()));
                for (Customer c : editOwnerCombo.getItems()) {
                    if (c.getCustomerId() == sel.getOwnerId()) {
                        editOwnerCombo.setValue(c);
                        break;
                    }
                }
            }
        });

        serviceTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editServiceVehicleId.setText(String.valueOf(sel.getVehicleId()));
                editServiceDate.setText(sel.getServiceDate());
                editServiceType.setText(sel.getServiceType());
                editServiceDesc.setText(sel.getDescription());
                editServiceCost.setText(String.valueOf(sel.getCost()));
            }
        });

        ValidationHelper.applyIntegerFilter(yearField);
        ValidationHelper.applyIntegerFilter(editYearField);
        ValidationHelper.applyIntegerFilter(sVehicleIdField);
        ValidationHelper.applyIntegerFilter(editServiceVehicleId);
        ValidationHelper.applyIntegerFilter(sCostField);
        ValidationHelper.applyIntegerFilter(editServiceCost);
        ValidationHelper.applyDateFilter(sDateField);
        ValidationHelper.applyDateFilter(editServiceDate);
    }

    private void loadOwnerComboBoxes() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"), rs.getString("name"),
                        rs.getString("address"), rs.getString("phone"),
                        rs.getString("email")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load customers: " + e.getMessage());
        }
        ownerCombo.setItems(customers);
        editOwnerCombo.setItems(customers);
    }

    private void setupVehicleTable() {
        vehicleTable.getColumns().clear();
        vehicleTable.getColumns().addAll(
                column("ID", "vehicleId", 50),
                column("Registration", "registrationNumber", 130),
                column("Make", "make", 100),
                column("Model", "model", 100),
                column("Year", "year", 70),
                column("Owner", "ownerName", 150)
        );
    }

    private void setupServiceTable() {
        serviceTable.getColumns().clear();
        serviceTable.getColumns().addAll(
                sCol("ID", "serviceId", 50),
                sCol("Vehicle ID", "vehicleId", 80),
                sCol("Date", "serviceDate", 100),
                sCol("Type", "serviceType", 120),
                sCol("Description", "description", 200),
                sCol("Cost (M)", "cost", 80)
        );
    }

    @FXML private void handleSearch() {
        String term = searchField.getText().trim();
        vehicleTable.setItems(term.isEmpty() ? getAllVehicles() : searchByRegistration(term));
    }

    @FXML private void handleRefresh() {
        loadAllVehicles();
        loadAllServices();
        searchField.clear();
    }

    @FXML private void handleAddVehicle() {
        Customer selectedOwner = ownerCombo.getValue();
        if (selectedOwner == null) {
            AlertHelper.showWarning("Validation", "Please select an owner.");
            return;
        }
        try {
            String reg = regField.getText().trim();
            String make = makeField.getText().trim();
            String model = modelField.getText().trim();
            int year = Integer.parseInt(yearField.getText().trim());
            if (reg.isEmpty() || make.isEmpty() || model.isEmpty()) {
                AlertHelper.showWarning("Validation", "All fields required.");
                return;
            }
            if (year > Year.now().getValue()) {
                AlertHelper.showWarning("Validation", "Year cannot be in the future.");
                return;
            }

            String sql = "INSERT INTO vehicles (registration_number, make, model, year, owner_id) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, reg);
                pst.setString(2, make);
                pst.setString(3, model);
                pst.setInt   (4, year);
                pst.setInt   (5, selectedOwner.getCustomerId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Vehicle added.");
                clearAddForm();
                loadAllVehicles();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Year must be a number.");
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not add vehicle: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateVehicle() {
        Vehicle sel = vehicleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a vehicle."); return; }
        Customer selectedOwner = editOwnerCombo.getValue();
        if (selectedOwner == null) {
            AlertHelper.showWarning("Validation", "Please select an owner.");
            return;
        }
        try {
            String reg = editRegField.getText().trim();
            String make = editMakeField.getText().trim();
            String model = editModelField.getText().trim();
            int year = Integer.parseInt(editYearField.getText().trim());
            if (reg.isEmpty() || make.isEmpty() || model.isEmpty()) {
                AlertHelper.showWarning("Validation", "All fields required.");
                return;
            }
            if (year > Year.now().getValue()) {
                AlertHelper.showWarning("Validation", "Year cannot be in the future.");
                return;
            }
            String sql = "UPDATE vehicles SET registration_number = ?, make = ?, model = ?, year = ?, owner_id = ? WHERE vehicle_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, reg);
                pst.setString(2, make);
                pst.setString(3, model);
                pst.setInt   (4, year);
                pst.setInt   (5, selectedOwner.getCustomerId());
                pst.setInt   (6, sel.getVehicleId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Vehicle updated.");
                clearEditVehicleForm();
                loadAllVehicles();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Year must be a number.");
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteVehicle() {
        Vehicle sel = vehicleTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a vehicle."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete " + sel.getRegistrationNumber() + "?")) return;
        try {
            String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, sel.getVehicleId());
                pst.executeUpdate();
                AlertHelper.showInfo("Deleted", "Vehicle removed.");
                clearEditVehicleForm();
                loadAllVehicles();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleCancelAddVehicle()   { clearAddForm(); ownerCombo.setValue(null); }
    @FXML private void handleCancelEditVehicle()  { clearEditVehicleForm(); editOwnerCombo.setValue(null); }
    @FXML private void handleCancelAddService()   { clearServiceAddForm(); }
    @FXML private void handleCancelEditService()  { clearEditServiceForm(); }

    @FXML private void handleAddService() {
        try {
            int vId = Integer.parseInt(sVehicleIdField.getText().trim());
            String date = sDateField.getText().trim();
            String type = sTypeField.getText().trim();
            String desc = sDescField.getText().trim();
            double cost = Double.parseDouble(sCostField.getText().trim());
            if (type.isEmpty() || date.isEmpty()) {
                AlertHelper.showWarning("Validation", "Date and Type are required.");
                return;
            }
            LocalDate serviceDate = Date.valueOf(date).toLocalDate();
            if (serviceDate.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Service date cannot be in the future.");
                return;
            }
            String sql = "INSERT INTO service_records (vehicle_id, service_date, service_type, description, cost) VALUES (?,?,?,?,?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, vId);
                pst.setDate(2, Date.valueOf(date));
                pst.setString(3, type);
                pst.setString(4, desc);
                pst.setDouble(5, cost);
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Service record added.");
                clearServiceAddForm();
                loadAllServices();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Vehicle ID and Cost must be numbers.");
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not add service: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateService() {
        ServiceRecord sel = serviceTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a service record."); return; }
        try {
            int vId = Integer.parseInt(editServiceVehicleId.getText().trim());
            String date = editServiceDate.getText().trim();
            String type = editServiceType.getText().trim();
            String desc = editServiceDesc.getText().trim();
            double cost = Double.parseDouble(editServiceCost.getText().trim());
            if (type.isEmpty() || date.isEmpty()) {
                AlertHelper.showWarning("Validation", "Date and Type are required.");
                return;
            }
            LocalDate serviceDate = Date.valueOf(date).toLocalDate();
            if (serviceDate.isAfter(LocalDate.now())) {
                AlertHelper.showWarning("Validation", "Service date cannot be in the future.");
                return;
            }
            String sql = "UPDATE service_records SET vehicle_id=?, service_date=?, service_type=?, description=?, cost=? WHERE service_id=?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, vId);
                pst.setDate(2, Date.valueOf(date));
                pst.setString(3, type);
                pst.setString(4, desc);
                pst.setDouble(5, cost);
                pst.setInt(6, sel.getServiceId());
                pst.executeUpdate();
                AlertHelper.showInfo("Success", "Service updated.");
                clearEditServiceForm();
                loadAllServices();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Vehicle ID and Cost must be numbers.");
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteService() {
        ServiceRecord sel = serviceTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a service record."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete this service record?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM service_records WHERE service_id=?")) {
            pst.setInt(1, sel.getServiceId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Service record removed.");
            clearEditServiceForm();
            loadAllServices();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    private ObservableList<Vehicle> getAllVehicles() {
        ObservableList<Vehicle> list = FXCollections.observableArrayList();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.owner_id, " +
                "c.name AS owner_name FROM vehicles v LEFT JOIN customers c ON v.owner_id = c.customer_id ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getInt("vehicle_id"), rs.getString("registration_number"),
                        rs.getString("make"), rs.getString("model"),
                        rs.getInt("year"), rs.getInt("owner_id"),
                        rs.getString("owner_name")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load vehicles: " + e.getMessage());
        }
        return list;
    }

    private ObservableList<Vehicle> searchByRegistration(String term) {
        ObservableList<Vehicle> list = FXCollections.observableArrayList();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.owner_id, " +
                "c.name AS owner_name FROM vehicles v LEFT JOIN customers c ON v.owner_id = c.customer_id " +
                "WHERE v.registration_number ILIKE ? ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + term + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getInt("vehicle_id"), rs.getString("registration_number"),
                        rs.getString("make"), rs.getString("model"),
                        rs.getInt("year"), rs.getInt("owner_id"),
                        rs.getString("owner_name")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Search failed: " + e.getMessage());
        }
        return list;
    }

    private void loadAllVehicles() { vehicleTable.setItems(getAllVehicles()); }

    private void loadAllServices() {
        ObservableList<ServiceRecord> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM service_records ORDER BY service_id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ServiceRecord(
                        rs.getInt("service_id"), rs.getInt("vehicle_id"),
                        rs.getString("service_date"), rs.getString("service_type"),
                        rs.getString("description"), rs.getDouble("cost")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load service records: " + e.getMessage());
        }
        serviceTable.setItems(list);
    }

    private void clearAddForm() {
        regField.clear(); makeField.clear(); modelField.clear();
        yearField.clear(); ownerCombo.setValue(null);
    }
    private void clearEditVehicleForm() {
        editRegField.clear(); editMakeField.clear(); editModelField.clear();
        editYearField.clear(); editOwnerCombo.setValue(null);
    }
    private void clearServiceAddForm() {
        sVehicleIdField.clear(); sDateField.clear(); sTypeField.clear();
        sDescField.clear(); sCostField.clear();
    }
    private void clearEditServiceForm() {
        editServiceVehicleId.clear(); editServiceDate.clear(); editServiceType.clear();
        editServiceDesc.clear(); editServiceCost.clear();
    }

    private <T> TableColumn<Vehicle, T> column(String title, String property, double width) {
        TableColumn<Vehicle, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }
    private <T> TableColumn<ServiceRecord, T> sCol(String title, String property, double width) {
        TableColumn<ServiceRecord, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }
}