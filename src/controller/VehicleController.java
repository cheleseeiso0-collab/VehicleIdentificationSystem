package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Customer;
import model.Vehicle;
import service.CustomerService;
import service.VehicleService;

import java.sql.SQLException;

import static controller.UIHelper.*;

public class VehicleController {

    private final ObservableList<Vehicle>  data      = FXCollections.observableArrayList();
    private final ObservableList<Customer> customers = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Vehicles  ");

        // ── table ─────────────────────────────────────────────
        TableView<Vehicle> table = new TableView<>(data);
        styleTable(table);

        TableColumn<Vehicle,String>  cReg     = col("Registration",  "registrationNumber");
        TableColumn<Vehicle,String>  cMake    = col("Make",           "make");
        TableColumn<Vehicle,String>  cModel   = col("Model",          "model");
        TableColumn<Vehicle,Integer> cYear    = new TableColumn<>("Year");
        cYear.setCellValueFactory(new PropertyValueFactory<>("year"));
        TableColumn<Vehicle,String>  cColor   = col("Color",          "color");
        TableColumn<Vehicle,String>  cChassis = col("Chassis No.",    "chassisNumber");
        TableColumn<Vehicle,String>  cOwner   = new TableColumn<>("Owner");
        cOwner.setCellValueFactory(cd ->
            new SimpleStringProperty(cd.getValue().getOwnerName() != null
                ? cd.getValue().getOwnerName() : "—"));
        table.getColumns().addAll(cReg, cMake, cModel, cYear, cColor, cChassis, cOwner);

        // ── search bar ────────────────────────────────────────
        TextField tfSearch  = field("Search by registration number...", 260);
        Button    btnSearch = secondaryBtn("Search");
        Button    btnClear  = mutedBtn("Clear");
        Label     errLbl    = errorLabel();

        btnSearch.setOnAction(e -> {
            errLbl.setText("");
            String q = tfSearch.getText().trim();
            if (q.isEmpty()) { refresh(); return; }
            try {
                Vehicle v = VehicleService.findByReg(q);
                data.clear();
                if (v != null) data.add(v);
                else errLbl.setText("No vehicle found for registration: " + q);
            } catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });
        btnClear.setOnAction(e -> { tfSearch.clear(); errLbl.setText(""); refresh(); });

        HBox searchBar = new HBox(8, tfSearch, btnSearch, btnClear);

        // ── add vehicle form ──────────────────────────────────
        TextField tfReg     = field("Registration No.", 150);
        TextField tfMake    = field("Make",              130);
        TextField tfModel   = field("Model",             130);
        TextField tfYear    = field("Year",               70);
        TextField tfColor   = field("Color",             110);
        TextField tfChassis = field("Chassis No.",       160);

        ComboBox<Customer> cbOwner = new ComboBox<>(customers);
        cbOwner.setPromptText("Select owner");
        cbOwner.setStyle(inputStyle());
        cbOwner.setPrefWidth(190);
        cbOwner.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Customer c) { return c == null ? "" : c.getName(); }
            public Customer fromString(String s) { return null; }
        });

        Button btnAdd     = primaryBtn("Add Vehicle");
        Button btnDel     = dangerBtn("Delete");
        Button btnRefresh = mutedBtn("Refresh");

        btnAdd.setOnAction(e -> {
            errLbl.setText("");
            if (tfReg.getText().isBlank() || tfMake.getText().isBlank() ||
                tfModel.getText().isBlank() || tfYear.getText().isBlank() ||
                cbOwner.getValue() == null) {
                errLbl.setText("Registration, make, model, year and owner are required.");
                return;
            }
            try {
                int year = Integer.parseInt(tfYear.getText().trim());
                VehicleService.register(
                    tfReg.getText().trim(), tfMake.getText().trim(),
                    tfModel.getText().trim(), year,
                    tfColor.getText().trim(), tfChassis.getText().trim(),
                    cbOwner.getValue().getId());
                tfReg.clear(); tfMake.clear(); tfModel.clear();
                tfYear.clear(); tfColor.clear(); tfChassis.clear();
                cbOwner.setValue(null);
                refresh();
            } catch (NumberFormatException ex) {
                errLbl.setText("Year must be a valid number.");
            } catch (SQLException ex) {
                errLbl.setText("Error: " + ex.getMessage());
            }
        });

        btnDel.setOnAction(e -> {
            Vehicle sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a vehicle from the table first."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete vehicle " + sel.getRegistrationNumber() + "?",
                ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
                try { VehicleService.delete(sel.getId()); refresh(); }
                catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
            });
        });

        btnRefresh.setOnAction(ev -> refresh());

        // form row
        HBox formRow = new HBox(8, tfReg, tfMake, tfModel, tfYear,
                                tfColor, tfChassis, cbOwner,
                                btnAdd, btnDel, btnRefresh);
        formRow.setStyle("-fx-alignment:CENTER_LEFT;");

        // ── form panel ────────────────────────────────────────
        VBox formPanel = new VBox(12,
            pageTitle("Vehicle Registry"),
            separator(),
            sectionLabel("Search"),
            searchBar,
            separator(),
            sectionLabel("Register New Vehicle"),
            formRow,
            errLbl
        );
        formPanel.setPadding(new Insets(16));
        formPanel.setStyle(formPanelStyle());
        applyCardShadow(formPanel);

        VBox layout = new VBox(14, formPanel, table);
        layout.setPadding(new Insets(16));
        layout.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(layout);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) { refresh(); refreshOwners(); }
        });
        refresh();
        refreshOwners();
        return tab;
    }

    private void refresh() {
        try { data.setAll(VehicleService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    private void refreshOwners() {
        try { customers.setAll(CustomerService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Vehicle, T> col(String title, String prop) {
        TableColumn<Vehicle, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }
}
