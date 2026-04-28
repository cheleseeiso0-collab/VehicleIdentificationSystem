package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.PoliceReport;
import model.Vehicle;
import model.Violation;
import service.PoliceService;
import service.VehicleService;

import java.sql.SQLException;
import static controller.UIHelper.*;

public class PoliceController {

    private final ObservableList<PoliceReport> reports    = FXCollections.observableArrayList();
    private final ObservableList<Violation>    violations = FXCollections.observableArrayList();
    private final ObservableList<Vehicle>      vehicles   = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Police  ");

        // ── reports table ─────────────────────────────────────
        TableView<PoliceReport> rTable = new TableView<>(reports);
        styleTable(rTable);
        rTable.getColumns().addAll(
            rCol("ID","id"), rCol("Vehicle ID","vehicleId"),
            rCol("Date","reportDate"), rCol("Type","reportType"),
            rCol("Officer","officerName"), rCol("Description","description")
        );

        // ── violations table ──────────────────────────────────
        TableView<Violation> vTable = new TableView<>(violations);
        styleTable(vTable);

        TableColumn<Violation,String> cStatus = new TableColumn<>("Status");
        cStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        cStatus.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(String v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(v);
                setStyle("Paid".equals(v)
                    ? "-fx-text-fill:" + SUCCESS + ";-fx-font-weight:bold;"
                    : "-fx-text-fill:" + DANGER  + ";-fx-font-weight:bold;");
            }
        });

        vTable.getColumns().addAll(
            vCol("ID","id"), vCol("Vehicle ID","vehicleId"),
            vCol("Date","violationDate"), vCol("Type","violationType"),
            vCol("Fine (M)","fineAmount"), cStatus
        );

        // ── shared vehicle combo ──────────────────────────────
        ComboBox<Vehicle> cbVehicle = new ComboBox<>(vehicles);
        cbVehicle.setPromptText("Select vehicle");
        cbVehicle.setStyle(inputStyle());
        cbVehicle.setPrefWidth(200);
        cbVehicle.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Vehicle v) { return v == null ? "" : v.getRegistrationNumber(); }
            public Vehicle fromString(String s) { return null; }
        });

        // ── report form ───────────────────────────────────────
        TextField tfRDate   = field("Date (YYYY-MM-DD)", 140);
        ComboBox<String> cbRType = typeCombo("Accident","Theft","Hit and Run","Other");
        TextField tfOfficer = field("Officer name", 150);
        TextArea  taDesc    = area("Description"); taDesc.setPrefRowCount(2); taDesc.setPrefWidth(200);
        Label     rErr      = errorLabel();
        Button    btnAddR   = primaryBtn("Add Report");

        btnAddR.setOnAction(e -> {
            rErr.setText("");
            if (cbVehicle.getValue()==null || tfRDate.getText().isBlank() ||
                tfOfficer.getText().isBlank()) {
                rErr.setText("Vehicle, date and officer name are required."); return;
            }
            try {
                PoliceService.addReport(cbVehicle.getValue().getId(),
                    tfRDate.getText().trim(), cbRType.getValue(),
                    taDesc.getText().trim(), tfOfficer.getText().trim());
                tfRDate.clear(); tfOfficer.clear(); taDesc.clear(); refresh();
            } catch (SQLException ex) { rErr.setText(ex.getMessage()); }
        });

        // ── violation form ────────────────────────────────────
        TextField tfVDate   = field("Date (YYYY-MM-DD)", 140);
        ComboBox<String> cbVType = typeCombo(
            "Speeding","Drunk Driving","No Licence","No Insurance",
            "Running Red Light","Reckless Driving","Other");
        TextField tfFine    = field("Fine amount (M)", 130);
        Label     vErr      = errorLabel();
        Button    btnAddV   = secondaryBtn("Add Violation");
        Button    btnPay    = successBtn("Mark Paid");
        Button    btnRefresh= mutedBtn("Refresh");

        btnAddV.setOnAction(e -> {
            vErr.setText("");
            if (cbVehicle.getValue()==null || tfVDate.getText().isBlank() ||
                tfFine.getText().isBlank()) {
                vErr.setText("Vehicle, date and fine amount are required."); return;
            }
            try {
                double fine = Double.parseDouble(tfFine.getText().trim());
                PoliceService.addViolation(cbVehicle.getValue().getId(),
                    tfVDate.getText().trim(), cbVType.getValue(), fine);
                tfVDate.clear(); tfFine.clear(); refresh();
            } catch (NumberFormatException ex) {
                vErr.setText("Fine must be a number.");
            } catch (SQLException ex) { vErr.setText(ex.getMessage()); }
        });

        btnPay.setOnAction(e -> {
            Violation sel = vTable.getSelectionModel().getSelectedItem();
            if (sel == null) { vErr.setText("Select a violation first."); return; }
            try { PoliceService.payViolation(sel.getId()); refresh(); }
            catch (SQLException ex) { vErr.setText(ex.getMessage()); }
        });

        btnRefresh.setOnAction(ev -> refresh());

        // ── layout ────────────────────────────────────────────
        HBox reportFormRow    = new HBox(8, cbVehicle, tfRDate, cbRType, tfOfficer, taDesc, btnAddR);
        HBox violationFormRow = new HBox(8, tfVDate, cbVType, tfFine, btnAddV, btnPay, btnRefresh);

        VBox formPanel = new VBox(12,
            pageTitle("Police Module"),
            separator(),
            sectionLabel("File Police Report"),
            reportFormRow, rErr,
            separator(),
            sectionLabel("Record Violation"),
            violationFormRow, vErr
        );
        formPanel.setPadding(new Insets(16));
        formPanel.setStyle(formPanelStyle());
        applyCardShadow(formPanel);

        VBox layout = new VBox(14,
            formPanel,
            sectionLabel("Police Reports"), rTable,
            sectionLabel("Violations"),     vTable
        );
        layout.setPadding(new Insets(16));
        layout.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(rTable, Priority.ALWAYS);
        VBox.setVgrow(vTable, Priority.ALWAYS);

        ScrollPane sp = new ScrollPane(layout);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background-color:" + BG + ";");
        tab.setContent(sp);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) { refresh(); refreshVehicles(); }
        });
        refresh();
        refreshVehicles();
        return tab;
    }

    private void refresh() {
        try {
            reports.setAll(PoliceService.getAllReports());
            violations.setAll(PoliceService.getAllViolations());
        } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    private void refreshVehicles() {
        try { vehicles.setAll(VehicleService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    private ComboBox<String> typeCombo(String... options) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(options);
        cb.setValue(options[0]);
        cb.setStyle(inputStyle());
        return cb;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<PoliceReport, T> rCol(String t, String p) {
        TableColumn<PoliceReport,T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p));
        return c;
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Violation, T> vCol(String t, String p) {
        TableColumn<Violation,T> c = new TableColumn<>(t);
        c.setCellValueFactory(new PropertyValueFactory<>(p));
        return c;
    }
}
