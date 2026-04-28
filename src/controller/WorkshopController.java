package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.ServiceRecord;
import model.Vehicle;
import service.VehicleService;
import service.WorkshopService;

import java.sql.SQLException;
import static controller.UIHelper.*;

public class WorkshopController {

    private final ObservableList<ServiceRecord> data     = FXCollections.observableArrayList();
    private final ObservableList<Vehicle>       vehicles = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Workshop  ");

        TableView<ServiceRecord> table = new TableView<>(data);
        styleTable(table);
        table.getColumns().addAll(
            col("ID",          "id"),
            col("Vehicle ID",  "vehicleId"),
            col("Date",        "serviceDate"),
            col("Type",        "serviceType"),
            col("Description", "description"),
            col("Cost (M)",    "cost")
        );

        ComboBox<Vehicle> cbVehicle = new ComboBox<>(vehicles);
        cbVehicle.setPromptText("Select vehicle");
        cbVehicle.setStyle(inputStyle());
        cbVehicle.setPrefWidth(210);
        cbVehicle.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getRegistrationNumber(); }
            public Vehicle fromString(String s) { return null; }
        });

        TextField tfDate = field("Date (YYYY-MM-DD)", 150);
        TextField tfType = field("Service type",       150);
        TextField tfCost = field("Cost (M)",            100);
        TextArea  taDesc = area("Description (optional)");
        taDesc.setPrefRowCount(2); taDesc.setPrefWidth(220);

        Label  errLbl    = errorLabel();
        Button btnAdd    = primaryBtn("Add Record");
        Button btnDel    = dangerBtn("Delete");
        Button btnRefresh= mutedBtn("Refresh");

        btnAdd.setOnAction(e -> {
            errLbl.setText("");
            if (cbVehicle.getValue() == null || tfDate.getText().isBlank() ||
                tfType.getText().isBlank()) {
                errLbl.setText("Vehicle, date and service type are required."); return;
            }
            try {
                double cost = tfCost.getText().isBlank() ? 0
                              : Double.parseDouble(tfCost.getText().trim());
                WorkshopService.addService(cbVehicle.getValue().getId(),
                    tfDate.getText().trim(), tfType.getText().trim(),
                    taDesc.getText().trim(), cost);
                tfDate.clear(); tfType.clear(); tfCost.clear(); taDesc.clear();
                refresh();
            } catch (NumberFormatException ex) {
                errLbl.setText("Cost must be a number.");
            } catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnDel.setOnAction(e -> {
            ServiceRecord sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a record first."); return; }
            try { WorkshopService.deleteService(sel.getId()); refresh(); }
            catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnRefresh.setOnAction(ev -> refresh());

        HBox formRow = new HBox(8, cbVehicle, tfDate, tfType, tfCost, taDesc,
                                btnAdd, btnDel, btnRefresh);

        VBox formPanel = new VBox(12, pageTitle("Workshop — Service Records"),
                                  separator(), formRow, errLbl);
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
        try { data.setAll(WorkshopService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    private void refreshVehicles() {
        try { vehicles.setAll(VehicleService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<ServiceRecord, T> col(String title, String prop) {
        TableColumn<ServiceRecord, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }
}
