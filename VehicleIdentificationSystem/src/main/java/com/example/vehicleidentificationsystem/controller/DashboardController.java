package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private BorderPane mainPane;
    @FXML private HBox navBar;

    @FXML private Button homeNavBtn, workshopNavBtn, customerNavBtn,
            policeNavBtn, insuranceNavBtn, adminNavBtn, buyVehicleBtn;

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isAdmin()) {
            adminNavBtn.setVisible(false);
            adminNavBtn.setManaged(false);
        }
        loadView("Home.fxml");
    }

    @FXML private void loadHome()       { loadView("Home.fxml"); }
    @FXML private void loadWorkshop()   { loadView("VehicleManagement.fxml"); }
    @FXML private void loadCustomer()   { loadView("CustomerManagement.fxml"); }
    @FXML private void loadPolice()     { loadView("PoliceManagement.fxml"); }
    @FXML private void loadInsurance()  { loadView("InsuranceManagement.fxml"); }
    @FXML private void loadBuyVehicle() { loadView("BuyerInquiry.fxml"); }
    @FXML private void loadAdmin()      {
        if (SessionManager.getInstance().isAdmin()) {
            loadView("AdminManagement.fxml");
        } else {
            AlertHelper.showWarning("Access Denied", "Admin only.");
        }
    }

    private void loadView(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/vehicleidentificationsystem/" + fxml));
            Parent view = loader.load();
            mainPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
            AlertHelper.showError("Error", "Could not load " + fxml);
        }
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        try {
            Stage stage = (Stage) mainPane.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/vehicleidentificationsystem/Login.fxml"));
            stage.setScene(new Scene(loader.load(), 860, 560));
            stage.setTitle("Vehicle Identification System – Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit() {
        if (AlertHelper.showConfirm("Exit", "Are you sure?"))
            ((Stage) mainPane.getScene().getWindow()).close();
    }
}