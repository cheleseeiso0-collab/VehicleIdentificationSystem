package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HomeController {

    @FXML private Label totalVehiclesLabel;
    @FXML private Label registeredOwnersLabel;
    @FXML private Label unpaidViolationsLabel;
    @FXML private Label activeModulesLabel;

    @FXML private ProgressBar vehicleRecordsProgress;
    @FXML private Label vehicleRecordsValue;
    @FXML private ProgressBar customerRecordsProgress;
    @FXML private Label customerRecordsValue;

    @FXML private Pagination pagination;

    private ObservableList<ActivityItem> activityItems;
    private ObservableList<RegistrationItem> registrationItems;

    @FXML
    public void initialize() {
        loadCounts();
        loadHealth();
        loadActivityData();
        loadRegistrationData();

        pagination.setPageCount(2);
        pagination.setPageFactory(this::createPage);
    }

    private void loadCounts() {
        int vehicles = getCount("SELECT COUNT(*) FROM vehicles");
        int customers = getCount("SELECT COUNT(*) FROM customers");
        int unpaid = getCount("SELECT COUNT(*) FROM violations WHERE status = 'Unpaid'");

        totalVehiclesLabel.setText(String.valueOf(vehicles));
        registeredOwnersLabel.setText(String.valueOf(customers));
        unpaidViolationsLabel.setText(String.valueOf(unpaid));
        activeModulesLabel.setText("5");
    }

    private void loadHealth() {
        int vehicleCount = getCount("SELECT COUNT(*) FROM vehicles");
        int customerCount = getCount("SELECT COUNT(*) FROM customers");

        double vp = Math.min(vehicleCount / 100.0, 1.0);
        double cp = Math.min(customerCount / 100.0, 1.0);

        vehicleRecordsProgress.setProgress(vp);
        vehicleRecordsValue.setText(String.format("%.0f%%", vp * 100));
        customerRecordsProgress.setProgress(cp);
        customerRecordsValue.setText(String.format("%.0f%%", cp * 100));
    }

    private void loadActivityData() {
        List<String> raw = fetchRealActivities();
        if (raw.isEmpty()) {
            raw = buildActivityItems();   
        }
        activityItems = FXCollections.observableArrayList();
        for (int i = 0; i < raw.size(); i++) {
            activityItems.add(new ActivityItem(i + 1, raw.get(i)));
        }
    }

    private void loadRegistrationData() {
        String[] regs = {
                "AAA 001 LS","BBB 202 LS","CCC 303 LS","DDD 404 LS","EEE 505 LS",
                "FFF 606 LS","GGG 707 LS","HHH 808 LS","III 909 LS","JJJ 100 LS",
                "KKK 111 LS","LLL 222 LS","MMM 333 LS","NNN 444 LS","OOO 555 LS",
                "PPP 666 LS","QQQ 777 LS","RRR 888 LS","SSS 999 LS","TTT 010 LS",
                "UUU 020 LS","VVV 030 LS","WWW 040 LS","XXX 050 LS","YYY 060 LS"
        };
        String[] makes = {
                "Toyota","Honda","Ford","VW","Hyundai","Mazda","Nissan","BMW","Mercedes","Kia",
                "Suzuki","Isuzu","Chevrolet","Peugeot","Renault","Mitsubishi","Subaru","Audi",
                "Land Rover","Jeep","Volvo","Lexus","Fiat","Seat","Opel"
        };
        registrationItems = FXCollections.observableArrayList();
        for (int i = 0; i < 25; i++) {
            registrationItems.add(new RegistrationItem(i + 1, regs[i], makes[i]));
        }
    }

    private VBox createPage(int pageIndex) {
        if (pageIndex == 0) {
            VBox box = new VBox(10);
            Text heading = new Text("Recent Activity");
            heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #1B2631;");
            box.getChildren().add(heading);
            box.setStyle("-fx-padding: 10;");

            TableView<ActivityItem> table = new TableView<>();
            TableColumn<ActivityItem, Integer> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
            idCol.setPrefWidth(60);

            TableColumn<ActivityItem, String> actCol = new TableColumn<>("Activity");
            actCol.setCellValueFactory(cellData -> cellData.getValue().activityProperty());
            actCol.setPrefWidth(500);

            table.getColumns().addAll(idCol, actCol);
            table.setItems(activityItems);
            table.setPrefHeight(200);

            box.getChildren().add(table);
            return box;
        } else {

            VBox box = new VBox(10);
            Text heading = new Text("Registration Index — 25 Items");
            heading.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-fill: #1B2631;");
            box.getChildren().add(heading);
            box.setStyle("-fx-padding: 10;");

            TableView<RegistrationItem> table = new TableView<>();
            TableColumn<RegistrationItem, Integer> noCol = new TableColumn<>("No.");
            noCol.setCellValueFactory(cellData -> cellData.getValue().noProperty().asObject());
            noCol.setPrefWidth(50);

            TableColumn<RegistrationItem, String> regCol = new TableColumn<>("Registration Number");
            regCol.setCellValueFactory(cellData -> cellData.getValue().registrationProperty());
            regCol.setPrefWidth(180);

            TableColumn<RegistrationItem, String> mkCol = new TableColumn<>("Make & Model");
            mkCol.setCellValueFactory(cellData -> cellData.getValue().makeModelProperty());
            mkCol.setPrefWidth(300);

            table.getColumns().addAll(noCol, regCol, mkCol);
            table.setItems(registrationItems);
            table.setPrefHeight(200);

            box.getChildren().add(table);
            return box;
        }
    }

    private List<String> fetchRealActivities() {
        List<String> activities = new ArrayList<>();

        String vehicleSQL = "SELECT v.registration_number, v.make, v.model, v.year, c.name " +
                "FROM vehicles v LEFT JOIN customers c ON v.owner_id = c.customer_id " +
                "ORDER BY v.vehicle_id DESC LIMIT 5";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(vehicleSQL)) {
            while (rs.next()) {
                String act = String.format("[WORKSHOP] Vehicle %s registered — %s %s %d (Owner: %s)",
                        rs.getString("registration_number"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("name") != null ? rs.getString("name") : "N/A");
                activities.add(act);
            }
        } catch (SQLException e) {  }


        String custSQL = "SELECT name, customer_id FROM customers ORDER BY customer_id DESC LIMIT 5";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(custSQL)) {
            while (rs.next()) {
                activities.add(String.format("[CUSTOMER] New owner registered: %s (ID %d)",
                        rs.getString("name"), rs.getInt("customer_id")));
            }
        } catch (SQLException e) { /* ignore */ }


        String reportSQL = "SELECT r.report_type, r.report_date, v.registration_number, r.officer_name " +
                "FROM police_reports r JOIN vehicles v ON r.vehicle_id = v.vehicle_id " +
                "ORDER BY r.report_date DESC LIMIT 5";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(reportSQL)) {
            while (rs.next()) {
                activities.add(String.format("[POLICE] %s report filed for %s on %s by %s",
                        rs.getString("report_type"),
                        rs.getString("registration_number"),
                        rs.getString("report_date"),
                        rs.getString("officer_name")));
            }
        } catch (SQLException e) { /* ignore */ }

        String violSQL = "SELECT v.registration_number, vi.violation_type, vi.violation_date, vi.fine_amount " +
                "FROM violations vi JOIN vehicles v ON vi.vehicle_id = v.vehicle_id " +
                "ORDER BY vi.violation_date DESC LIMIT 5";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(violSQL)) {
            while (rs.next()) {
                activities.add(String.format("[VIOLATION] %s – %s on %s, Fine: M%.2f",
                        rs.getString("registration_number"),
                        rs.getString("violation_type"),
                        rs.getString("violation_date"),
                        rs.getDouble("fine_amount")));
            }
        } catch (SQLException e) { /* ignore */ }

        String insSQL = "SELECT p.provider, p.policy_number, v.registration_number, p.start_date " +
                "FROM insurance_policies p JOIN vehicles v ON p.vehicle_id = v.vehicle_id " +
                "ORDER BY p.policy_id DESC LIMIT 3";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(insSQL)) {
            while (rs.next()) {
                activities.add(String.format("[INSURANCE] %s policy %s for %s, effective %s",
                        rs.getString("provider"),
                        rs.getString("policy_number"),
                        rs.getString("registration_number"),
                        rs.getString("start_date")));
            }
        } catch (SQLException e) { /* ignore */ }

        String servSQL = "SELECT s.service_type, s.service_date, v.registration_number " +
                "FROM service_records s JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                "ORDER BY s.service_id DESC LIMIT 5";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(servSQL)) {
            while (rs.next()) {
                activities.add(String.format("[WORKSHOP] %s performed on %s (%s)",
                        rs.getString("service_type"),
                        rs.getString("registration_number"),
                        rs.getString("service_date")));
            }
        } catch (SQLException e) { /* ignore */ }

        return activities;
    }

    private int getCount(String sql) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private List<String> buildActivityItems() {
        List<String> items = new ArrayList<>();
        items.add("[WORKSHOP] Vehicle AAA 001 LS registered — Toyota Hilux 2020");
        items.add("[CUSTOMER] Owner Thabo Nkosi added");
        items.add("[POLICE] Report filed: Theft of AAA 001 LS");
        items.add("[WORKSHOP] Service record added — Oil change AAA 001 LS");
        items.add("[INSURANCE] Policy created for AAA 001 LS");
        items.add("[VIOLATION] Speeding fine M500 – AAA 001 LS");
        String[] actions = {
                "[CUSTOMER] Second-hand buyer enquiry — NNN 444 LS",
                "[POLICE]   Roadblock stop — OOO 555 LS — Passed"
        };
        for (String a : actions) items.add(a);
        return items;
    }

    public static class ActivityItem {
        private final javafx.beans.property.IntegerProperty id = new javafx.beans.property.SimpleIntegerProperty();
        private final javafx.beans.property.StringProperty activity = new javafx.beans.property.SimpleStringProperty();
        public ActivityItem(int id, String activity) { this.id.set(id); this.activity.set(activity); }
        public javafx.beans.property.IntegerProperty idProperty() { return id; }
        public javafx.beans.property.StringProperty activityProperty() { return activity; }
    }

    public static class RegistrationItem {
        private final javafx.beans.property.IntegerProperty no = new javafx.beans.property.SimpleIntegerProperty();
        private final javafx.beans.property.StringProperty registration = new javafx.beans.property.SimpleStringProperty();
        private final javafx.beans.property.StringProperty makeModel = new javafx.beans.property.SimpleStringProperty();
        public RegistrationItem(int no, String registration, String make) { this.no.set(no); this.registration.set(registration); this.makeModel.set(make); }
        public javafx.beans.property.IntegerProperty noProperty() { return no; }
        public javafx.beans.property.StringProperty registrationProperty() { return registration; }
        public javafx.beans.property.StringProperty makeModelProperty() { return makeModel; }
    }
}
