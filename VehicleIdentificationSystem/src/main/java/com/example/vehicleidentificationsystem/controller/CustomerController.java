package com.example.vehicleidentificationsystem.controller;

import com.example.vehicleidentificationsystem.config.DatabaseManager;
import com.example.vehicleidentificationsystem.model.Customer;
import com.example.vehicleidentificationsystem.model.CustomerQuery;
import com.example.vehicleidentificationsystem.util.AlertHelper;
import com.example.vehicleidentificationsystem.util.ValidationHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;

public class CustomerController {

    @FXML private TextField addNameField, addAddressField, addPhoneField, addEmailField;
    @FXML private TextField editNameField, editAddressField, editPhoneField, editEmailField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TextField inquiryCustomerName, inquiryVehicleId;
    @FXML private TextArea inquiryText;
    @FXML private TableView<CustomerQuery> inquiryTable;

    private int currentUserId;

    @FXML
    public void initialize() {
        currentUserId = com.example.vehicleidentificationsystem.util.SessionManager.getInstance()
                .getCurrentUser().getUserId();
        inquiryCustomerName.clear();

        setupCustomerTable();
        setupInquiryTable();
        loadCustomers();
        loadInquiries();

        if (customerTable.getItems().isEmpty()) {
            seedSampleData();
            loadCustomers();
            loadInquiries();
        }

        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) {
                editNameField.setText(sel.getName());
                editAddressField.setText(sel.getAddress());
                editPhoneField.setText(sel.getPhone());
                editEmailField.setText(sel.getEmail());
            }
        });

        ValidationHelper.applyNameFilter(addNameField);
        ValidationHelper.applyNameFilter(editNameField);
        ValidationHelper.applyNameFilter(inquiryCustomerName);
        ValidationHelper.applyPhoneFilter(addPhoneField);
        ValidationHelper.applyPhoneFilter(editPhoneField);
        ValidationHelper.applyIntegerFilter(inquiryVehicleId);
    }

    private void seedSampleData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO customers (name, address, phone, email) VALUES (?,?,?,?)")) {
                pst.setString(1, "Thabo Nkosi");
                pst.setString(2, "Maseru 100");
                pst.setString(3, "+26663245120");
                pst.setString(4, "thabo@gmail.com");
                pst.executeUpdate();
                pst.clearParameters();
                pst.setString(1, "Lineo Motsoai");
                pst.setString(2, "Leribe 200");
                pst.setString(3, "+26657712345");
                pst.setString(4, "lineo@gmail.com");
                pst.executeUpdate();
            }
            try (PreparedStatement pst = conn.prepareStatement(
                    "INSERT INTO customer_queries (customer_id, vehicle_id, query_date, query_text, response_text) VALUES (?, ?, CURRENT_DATE, ?, '')")) {
                pst.setInt(1, currentUserId);
                pst.setInt(2, 1);
                pst.setString(3, "How far is my vehicle service?");
                pst.executeUpdate();
            }
        } catch (SQLException e) {
            AlertHelper.showError("Seed Error", "Could not create sample data: " + e.getMessage());
        }
    }

    private void setupCustomerTable() {
        customerTable.getColumns().clear();
        customerTable.getColumns().addAll(
                custCol("ID", "customerId", 50),
                custCol("Name", "name", 150),
                custCol("Address", "address", 200),
                custCol("Phone", "phone", 120),
                custCol("Email", "email", 180)
        );
    }

    private void setupInquiryTable() {
        inquiryTable.getColumns().clear();
        inquiryTable.getColumns().addAll(
                inqCol("ID", "queryId", 50),
                inqCol("Customer ID", "customerId", 80),
                inqCol("Customer Name", "customerName", 120),
                inqCol("Vehicle", "vehicleId", 80),
                inqCol("Date", "queryDate", 100),
                inqCol("Message", "queryText", 200),
                inqCol("Response", "responseText", 200)
        );
    }

    @FXML private void handleAddCustomer() {
        String name = addNameField.getText().trim();
        if (name.isEmpty()) { AlertHelper.showWarning("Validation", "Name is required."); return; }

        String email = addEmailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            AlertHelper.showWarning("Validation", "Email must contain '@'.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO customers (name, address, phone, email) VALUES (?,?,?,?)")) {
            pst.setString(1, name);
            pst.setString(2, addAddressField.getText().trim());
            pst.setString(3, addPhoneField.getText().trim());
            pst.setString(4, email);
            pst.executeUpdate();
            AlertHelper.showInfo("Success", "Customer added.");
            clearAddFields();
            loadCustomers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Add failed: " + e.getMessage());
        }
    }

    @FXML private void handleUpdateCustomer() {
        Customer sel = customerTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a customer to update."); return; }
        String name = editNameField.getText().trim();
        if (name.isEmpty()) { AlertHelper.showWarning("Validation", "Name is required."); return; }

        String email = editEmailField.getText().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            AlertHelper.showWarning("Validation", "Email must contain '@'.");
            return;
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "UPDATE customers SET name = ?, address = ?, phone = ?, email = ? WHERE customer_id = ?")) {
            pst.setString(1, name);
            pst.setString(2, editAddressField.getText().trim());
            pst.setString(3, editPhoneField.getText().trim());
            pst.setString(4, email);
            pst.setInt(5, sel.getCustomerId());
            pst.executeUpdate();
            AlertHelper.showInfo("Success", "Customer updated.");
            clearEditFields();
            loadCustomers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML private void handleDeleteCustomer() {
        Customer sel = customerTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose a customer."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete " + sel.getName() + "?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
            pst.setInt(1, sel.getCustomerId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Customer removed.");
            clearEditFields();
            loadCustomers();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleSendInquiry() {
        String vehIdStr = inquiryVehicleId.getText().trim();
        String message  = inquiryText.getText().trim();
        if (vehIdStr.isEmpty() || message.isEmpty()) {
            AlertHelper.showWarning("Validation", "Vehicle ID and message are required.");
            return;
        }
        int vehicleId;
        try { vehicleId = Integer.parseInt(vehIdStr); }
        catch (NumberFormatException e) {
            AlertHelper.showWarning("Validation", "Vehicle ID must be a number.");
            return;
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement(
                     "INSERT INTO customer_queries (customer_id, vehicle_id, query_date, query_text, response_text) VALUES (?, ?, CURRENT_DATE, ?, '')")) {
            pst.setInt(1, currentUserId);
            pst.setInt(2, vehicleId);
            pst.setString(3, message);
            pst.executeUpdate();
            AlertHelper.showInfo("Sent", "Your inquiry has been sent.");
            clearInquiryFields();
            loadInquiries();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Could not send inquiry: " + e.getMessage());
        }
    }

    @FXML private void handleRespondInquiry() {
        CustomerQuery sel = inquiryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose an inquiry first."); return; }
        TextInputDialog dialog = new TextInputDialog(sel.getResponseText());
        dialog.setTitle("Response");
        dialog.setHeaderText("Respond to inquiry #" + sel.getQueryId());
        dialog.setContentText("Your response:");
        dialog.showAndWait().ifPresent(response -> {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(
                         "UPDATE customer_queries SET response_text = ? WHERE query_id = ?")) {
                pst.setString(1, response);
                pst.setInt(2, sel.getQueryId());
                pst.executeUpdate();
                AlertHelper.showInfo("Saved", "Response updated.");
                loadInquiries();
            } catch (SQLException e) {
                AlertHelper.showError("Error", "Could not update response: " + e.getMessage());
            }
        });
    }

    @FXML private void handleEditInquiry() {
        CustomerQuery sel = inquiryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose an inquiry to edit."); return; }

        Dialog<CustomerQuery> dialog = new Dialog<>();
        dialog.setTitle("Edit Inquiry");
        dialog.setHeaderText("Edit inquiry #" + sel.getQueryId());
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(8);
        grid.setPadding(new javafx.geometry.Insets(20));

        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) allCustomers.add(new Customer(
                    rs.getInt("customer_id"), rs.getString("name"),
                    rs.getString("address"), rs.getString("phone"),
                    rs.getString("email")));
        } catch (SQLException e) { /* ignore */ }

        ComboBox<Customer> custCombo = new ComboBox<>(allCustomers);
        for (Customer c : allCustomers) {
            if (c.getCustomerId() == sel.getCustomerId()) {
                custCombo.setValue(c);
                break;
            }
        }

        TextField vehField = new TextField(String.valueOf(sel.getVehicleId()));
        ValidationHelper.applyIntegerFilter(vehField);

        DatePicker datePicker = new DatePicker();
        try {
            LocalDate existingDate = LocalDate.parse(sel.getQueryDate());
            datePicker.setValue(existingDate);
        } catch (Exception e) {
            datePicker.setValue(LocalDate.now());
            AlertHelper.showWarning("Date Warning", "The stored date could not be read; it has been set to today.");
        }
        ValidationHelper.applyDateFilter(datePicker.getEditor());
        datePicker.getEditor().setEditable(true);
        TextArea msgArea = new TextArea(sel.getQueryText());
        msgArea.setPrefRowCount(3);
        msgArea.setWrapText(true);

        grid.add(new Label("Customer:"), 0, 0);
        grid.add(custCombo, 1, 0);
        grid.add(new Label("Vehicle ID:"), 0, 1);
        grid.add(vehField, 1, 1);
        grid.add(new Label("Date:"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Message:"), 0, 3);
        grid.add(msgArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Customer cust = custCombo.getValue();
                if (cust == null) return null;
                int newVehId;
                try { newVehId = Integer.parseInt(vehField.getText().trim()); }
                catch (NumberFormatException e) { return null; }
                LocalDate newDate = datePicker.getValue();
                if (newDate == null) return null;
                if (newDate.isAfter(LocalDate.now())) {
                    AlertHelper.showWarning("Validation", "Date cannot be in the future.");
                    return null;
                }
                String newMsg = msgArea.getText().trim();
                if (newMsg.isEmpty()) return null;
                sel.setCustomerId(cust.getCustomerId());
                sel.setVehicleId(newVehId);
                sel.setQueryDate(newDate.toString());
                sel.setQueryText(newMsg);
                return sel;
            }
            return null;
        });

        Optional<CustomerQuery> result = dialog.showAndWait();
        result.ifPresent(updated -> {
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pst = conn.prepareStatement(
                         "UPDATE customer_queries SET customer_id=?, vehicle_id=?, query_date=?, query_text=? WHERE query_id=?")) {
                pst.setInt(1, updated.getCustomerId());
                pst.setInt(2, updated.getVehicleId());
                pst.setDate(3, Date.valueOf(updated.getQueryDate()));
                pst.setString(4, updated.getQueryText());
                pst.setInt(5, updated.getQueryId());
                pst.executeUpdate();
                AlertHelper.showInfo("Updated", "Inquiry updated successfully.");
                loadInquiries();
            } catch (SQLException e) {
                AlertHelper.showError("Error", "Could not edit inquiry: " + e.getMessage());
            }
        });
    }

    @FXML private void handleDeleteInquiry() {
        CustomerQuery sel = inquiryTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select", "Choose an inquiry to delete."); return; }
        if (!AlertHelper.showConfirm("Delete", "Delete inquiry #" + sel.getQueryId() + "?")) return;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pst = conn.prepareStatement("DELETE FROM customer_queries WHERE query_id = ?")) {
            pst.setInt(1, sel.getQueryId());
            pst.executeUpdate();
            AlertHelper.showInfo("Deleted", "Inquiry removed.");
            loadInquiries();
        } catch (SQLException e) {
            AlertHelper.showError("Error", "Delete failed: " + e.getMessage());
        }
    }

    @FXML private void handleCancelAddCustomer()   { clearAddFields(); }
    @FXML private void handleCancelEditCustomer()  { clearEditFields(); }
    @FXML private void handleCancelSendInquiry()   { clearInquiryFields(); }

    private void clearAddFields() {
        addNameField.clear(); addAddressField.clear(); addPhoneField.clear(); addEmailField.clear();
    }

    private void clearEditFields() {
        editNameField.clear(); editAddressField.clear(); editPhoneField.clear(); editEmailField.clear();
    }

    private void clearInquiryFields() {
        inquiryCustomerName.clear(); inquiryVehicleId.clear(); inquiryText.clear();
    }

    private void loadCustomers() {
        ObservableList<Customer> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY customer_id")) {
            while (rs.next()) {
                list.add(new Customer(
                        rs.getInt("customer_id"), rs.getString("name"),
                        rs.getString("address"), rs.getString("phone"),
                        rs.getString("email")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load customers: " + e.getMessage());
        }
        customerTable.setItems(list);
    }

    private void loadInquiries() {
        ObservableList<CustomerQuery> list = FXCollections.observableArrayList();
        String sql = "SELECT cq.*, cu.name AS customer_name " +
                "FROM customer_queries cq " +
                "LEFT JOIN customers cu ON cq.customer_id = cu.customer_id " +
                "ORDER BY cq.query_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new CustomerQuery(
                        rs.getInt("query_id"),
                        rs.getInt("customer_id"),
                        rs.getString("customer_name"),
                        rs.getInt("vehicle_id"),
                        rs.getString("query_date"),
                        rs.getString("query_text"),
                        rs.getString("response_text")));
            }
        } catch (SQLException e) {
            AlertHelper.showError("DB Error", "Could not load inquiries: " + e.getMessage());
        }
        inquiryTable.setItems(list);
    }

    private <T> TableColumn<Customer, T> custCol(String title, String prop, int width) {
        TableColumn<Customer, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }
    private <T> TableColumn<CustomerQuery, T> inqCol(String title, String prop, int width) {
        TableColumn<CustomerQuery, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setPrefWidth(width);
        return col;
    }
}