package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.Insurance;
import model.Vehicle;
import service.InsuranceService;
import service.VehicleService;

import java.sql.SQLException;
import static controller.UIHelper.*;

public class InsuranceController {

    private final ObservableList<Insurance> data     = FXCollections.observableArrayList();
    private final ObservableList<Vehicle>   vehicles = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Insurance  ");

        // ── table ─────────────────────────────────────────────
        TableView<Insurance> table = new TableView<>(data);
        styleTable(table);

        TableColumn<Insurance,Integer> cId      = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Insurance,Integer> cVeh     = new TableColumn<>("Vehicle ID");
        cVeh.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));

        TableColumn<Insurance,String>  cProv    = col("Provider",       "provider");
        TableColumn<Insurance,String>  cPolicy  = col("Policy No.",     "policyNumber");
        TableColumn<Insurance,String>  cCov     = col("Coverage",       "coverageType");
        TableColumn<Insurance,String>  cStart   = col("Start Date",     "startDate");
        TableColumn<Insurance,String>  cExpiry  = col("Expiry Date",    "expiryDate");
        TableColumn<Insurance,Double>  cPrem    = new TableColumn<>("Premium (M)");
        cPrem.setCellValueFactory(new PropertyValueFactory<>("premiumAmount"));
        cPrem.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.format("M %,.2f", v));
            }
        });

        TableColumn<Insurance,String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle(switch(v) {
                    case "Active"    -> "-fx-text-fill:" + SUCCESS + ";-fx-font-weight:bold;";
                    case "Expired"   -> "-fx-text-fill:" + DANGER  + ";-fx-font-weight:bold;";
                    case "Cancelled" -> "-fx-text-fill:" + TEXT_MUTED + ";";
                    default          -> "";
                });
            }
        });

        table.getColumns().addAll(cId, cVeh, cProv, cPolicy, cCov,
                                  cStart, cExpiry, cPrem, cStatus);

        // ── form ──────────────────────────────────────────────
        ComboBox<Vehicle> cbVehicle = new ComboBox<>(vehicles);
        cbVehicle.setPromptText("Select vehicle");
        cbVehicle.setStyle(inputStyle());
        cbVehicle.setPrefWidth(200);
        cbVehicle.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getRegistrationNumber(); }
            public Vehicle fromString(String s) { return null; }
        });

        TextField tfProvider = field("Provider",           160);
        TextField tfPolicy   = field("Policy number",      160);
        TextField tfStart    = field("Start (YYYY-MM-DD)", 140);
        TextField tfExpiry   = field("Expiry (YYYY-MM-DD)",140);

        ComboBox<String> cbCoverage = new ComboBox<>();
        cbCoverage.getItems().addAll("Comprehensive","Third Party","Third Party Fire & Theft");
        cbCoverage.setValue("Comprehensive");
        cbCoverage.setStyle(inputStyle());
        cbCoverage.setPrefWidth(200);

        TextField tfPremium  = field("Premium (M)",  110);
        Label     errLbl     = errorLabel();

        Button btnAdd     = primaryBtn("Add Policy");
        Button btnExpire  = dangerBtn("Mark Expired");
        Button btnCancel  = mutedBtn("Cancel Policy");
        Button btnRefresh = mutedBtn("Refresh");

        btnAdd.setOnAction(e -> {
            errLbl.setText("");
            if (cbVehicle.getValue() == null || tfProvider.getText().isBlank() ||
                tfPolicy.getText().isBlank() || tfStart.getText().isBlank() ||
                tfExpiry.getText().isBlank()) {
                errLbl.setText("Vehicle, provider, policy number, start and expiry are required.");
                return;
            }
            try {
                double premium = tfPremium.getText().isBlank() ? 0
                                 : Double.parseDouble(tfPremium.getText().trim());
                InsuranceService.add(cbVehicle.getValue().getId(),
                    tfProvider.getText().trim(), tfPolicy.getText().trim(),
                    tfStart.getText().trim(), tfExpiry.getText().trim(),
                    cbCoverage.getValue(), premium);
                tfProvider.clear(); tfPolicy.clear(); tfStart.clear();
                tfExpiry.clear(); tfPremium.clear(); cbVehicle.setValue(null);
                refresh();
            } catch (NumberFormatException ex) {
                errLbl.setText("Premium must be a number.");
            } catch (SQLException ex) {
                errLbl.setText("Error: " + ex.getMessage());
            }
        });

        btnExpire.setOnAction(e -> {
            Insurance sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a policy first."); return; }
            try { InsuranceService.updateStatus(sel.getId(), "Expired"); refresh(); }
            catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnCancel.setOnAction(e -> {
            Insurance sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a policy first."); return; }
            try { InsuranceService.updateStatus(sel.getId(), "Cancelled"); refresh(); }
            catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnRefresh.setOnAction(ev -> refresh());

        HBox row1 = new HBox(8, cbVehicle, tfProvider, tfPolicy, cbCoverage, tfPremium);
        HBox row2 = new HBox(8, tfStart, tfExpiry, btnAdd, btnExpire, btnCancel, btnRefresh);

        VBox formPanel = new VBox(12,
            pageTitle("Insurance Management"),
            separator(),
            sectionLabel("Add New Policy"),
            row1, row2, errLbl
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
            if (tab.isSelected()) { refresh(); refreshVehicles(); }
        });
        refresh();
        refreshVehicles();
        return tab;
    }

    private void refresh() {
        try { data.setAll(InsuranceService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    private void refreshVehicles() {
        try { vehicles.setAll(VehicleService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Insurance, T> col(String title, String prop) {
        TableColumn<Insurance, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }
}
