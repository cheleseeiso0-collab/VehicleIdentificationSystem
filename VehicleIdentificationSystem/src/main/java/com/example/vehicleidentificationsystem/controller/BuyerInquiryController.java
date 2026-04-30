package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.Vehicle;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.SessionManager;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.sql.*;

public class BuyerInquiryController {
    @FXML private TextField searchField;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private Label selectedVehicleLabel, statusLabel;
    @FXML private TextArea inquiryText;
    @FXML private Button sendInquiryBtn;

    private Vehicle selectedVehicle;

    @FXML
    public void initialize() {
        vehicleTable.getColumns().clear();
        vehicleTable.getColumns().addAll(
                column("ID", "vehicleId", 50),
                column("Registration", "registrationNumber", 130),
                column("Make", "make", 100),
                column("Model", "model", 100),
                column("Year", "year", 70),
                column("Owner", "ownerName", 150)
        );
        loadAllVehicles();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            selectedVehicle = sel;
            if (sel != null) {
                selectedVehicleLabel.setText(sel.getRegistrationNumber() + " - " + sel.getMake() + " " + sel.getModel() + " (Owner: " + sel.getOwnerName() + ")");
            } else {
                selectedVehicleLabel.setText("");
            }
        });

        FadeTransition fade = new FadeTransition(Duration.seconds(1.4), sendInquiryBtn);
        fade.setFromValue(1.0);
        fade.setToValue(0.35);
        fade.setAutoReverse(true);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.play();
    }

    @FXML private void handleSearch() {
        String term = searchField.getText().trim();
        if (term.isEmpty()) {
            loadAllVehicles();
            return;
        }
        vehicleTable.setItems(searchVehicles(term));
    }

    @FXML private void handleShowAll() {
        loadAllVehicles();
        searchField.clear();
    }

    @FXML private void handleSendInquiry() {
        if (selectedVehicle == null) {
            statusLabel.setText("Please select a vehicle first.");
            return;
        }
        String text = inquiryText.getText().trim();
        if (text.isEmpty()) {
            statusLabel.setText("Inquiry message cannot be empty.");
            return;
        }
        int currentUserId = SessionManager.getInstance().getCurrentUser().getUserId();
        String sql = "INSERT INTO customer_queries (customer_id, vehicle_id, query_date, query_text, response_text) VALUES (?, ?, CURRENT_DATE, ?, '')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, currentUserId);
            pst.setInt(2, selectedVehicle.getVehicleId());
            pst.setString(3, text);
            pst.executeUpdate();
            AlertHelper.showInfo("Inquiry Sent", "Your message has been sent to the vehicle owner.");
            clearInquiryFields();
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
        }
    }

    @FXML private void handleCancel() {
        clearInquiryFields();
    }

    private void clearInquiryFields() {
        inquiryText.clear();
        statusLabel.setText("");
        selectedVehicleLabel.setText("");
    }

    private void loadAllVehicles() {
        vehicleTable.setItems(getAllVehicles());
    }

    private ObservableList<Vehicle> getAllVehicles() {
        ObservableList<Vehicle> list = FXCollections.observableArrayList();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.owner_id, " +
                "c.name AS owner_name FROM vehicles v LEFT JOIN customers c ON v.owner_id = c.customer_id ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getInt("vehicle_id"), rs.getString("registration_number"),
                        rs.getString("make"), rs.getString("model"),
                        rs.getInt("year"), rs.getInt("owner_id"), rs.getString("owner_name")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Could not load vehicles: " + e.getMessage());
        }
        return list;
    }

    private ObservableList<Vehicle> searchVehicles(String term) {
        ObservableList<Vehicle> list = FXCollections.observableArrayList();
        String sql = "SELECT v.vehicle_id, v.registration_number, v.make, v.model, v.year, v.owner_id, " +
                "c.name AS owner_name FROM vehicles v LEFT JOIN customers c ON v.owner_id = c.customer_id " +
                "WHERE v.registration_number ILIKE ? OR v.make ILIKE ? ORDER BY v.vehicle_id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + term + "%");
            pst.setString(2, "%" + term + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new Vehicle(
                        rs.getInt("vehicle_id"), rs.getString("registration_number"),
                        rs.getString("make"), rs.getString("model"),
                        rs.getInt("year"), rs.getInt("owner_id"), rs.getString("owner_name")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("Database Error", "Search failed: " + e.getMessage());
        }
        return list;
    }

    private <T> TableColumn<Vehicle, T> column(String title, String property, double width) {
        TableColumn<Vehicle, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setPrefWidth(width);
        return col;
    }
}