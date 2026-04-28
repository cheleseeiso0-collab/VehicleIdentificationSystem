package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.Customer;
import service.CustomerService;

import java.sql.SQLException;
import static controller.UIHelper.*;

public class CustomerController {

    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Customers  ");

        TableView<Customer> table = new TableView<>(data);
        styleTable(table);
        table.getColumns().addAll(
            col("ID",      "id"),
            col("Name",    "name"),
            col("Address", "address"),
            col("Phone",   "phone"),
            col("Email",   "email")
        );

        TextField tfName    = field("Full name",    180);
        TextField tfAddress = field("Address",      200);
        TextField tfPhone   = field("Phone number", 140);
        TextField tfEmail   = field("Email address",180);
        Label     errLbl    = errorLabel();

        Button btnAdd     = primaryBtn("Add Customer");
        Button btnUpdate  = secondaryBtn("Update");
        Button btnDelete  = dangerBtn("Delete");
        Button btnRefresh = mutedBtn("Refresh");

        btnAdd.setOnAction(e -> {
            errLbl.setText("");
            if (tfName.getText().isBlank()) { errLbl.setText("Name is required."); return; }
            try {
                CustomerService.add(tfName.getText().trim(), tfAddress.getText().trim(),
                    tfPhone.getText().trim(), tfEmail.getText().trim());
                tfName.clear(); tfAddress.clear(); tfPhone.clear(); tfEmail.clear();
                refresh();
            } catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnUpdate.setOnAction(e -> {
            Customer sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a customer first."); return; }
            if (tfName.getText().isBlank()) { errLbl.setText("Name is required."); return; }
            try {
                CustomerService.update(sel.getId(), tfName.getText().trim(),
                    tfAddress.getText().trim(), tfPhone.getText().trim(),
                    tfEmail.getText().trim());
                refresh();
            } catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnDelete.setOnAction(e -> {
            Customer sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a customer first."); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete customer " + sel.getName() + "?", ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText(null);
            confirm.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
                try { CustomerService.delete(sel.getId()); refresh(); }
                catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
            });
        });

        btnRefresh.setOnAction(ev -> refresh());

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                tfName.setText(sel.getName());
                tfAddress.setText(sel.getAddress() != null ? sel.getAddress() : "");
                tfPhone.setText(sel.getPhone()   != null ? sel.getPhone()   : "");
                tfEmail.setText(sel.getEmail()   != null ? sel.getEmail()   : "");
            }
        });

        HBox formRow = new HBox(8, tfName, tfAddress, tfPhone, tfEmail,
                                btnAdd, btnUpdate, btnDelete, btnRefresh);

        VBox formPanel = new VBox(12, pageTitle("Customer Management"),
                                  separator(), formRow, errLbl);
        formPanel.setPadding(new Insets(16));
        formPanel.setStyle(formPanelStyle());
        applyCardShadow(formPanel);

        VBox layout = new VBox(14, formPanel, table);
        layout.setPadding(new Insets(16));
        layout.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(table, Priority.ALWAYS);

        tab.setContent(layout);
        tab.setOnSelectionChanged(e -> { if (tab.isSelected()) refresh(); });
        refresh();
        return tab;
    }

    private void refresh() {
        try { data.setAll(CustomerService.getAll()); }
        catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }

    @SuppressWarnings("unchecked")
    private <T> TableColumn<Customer, T> col(String title, String prop) {
        TableColumn<Customer, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(prop));
        return c;
    }
}
