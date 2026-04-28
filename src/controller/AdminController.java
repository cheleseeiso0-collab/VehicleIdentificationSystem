package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import model.AppUser;
import service.AuthService;
import service.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import static controller.UIHelper.*;

public class AdminController {

    private final ObservableList<AppUser> data = FXCollections.observableArrayList();

    public Tab buildTab() {
        Tab tab = new Tab("  Admin  ");

        TableView<AppUser> table = new TableView<>(data);
        styleTable(table);

        TableColumn<AppUser,Integer> cId     = new TableColumn<>("ID");
        cId.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<AppUser,String>  cUser   = new TableColumn<>("Username");
        cUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        TableColumn<AppUser,String>  cRole   = new TableColumn<>("Role");
        cRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<AppUser,Boolean> cActive = new TableColumn<>("Active");
        cActive.setCellValueFactory(new PropertyValueFactory<>("active"));
        table.getColumns().addAll(cId, cUser, cRole, cActive);

        TextField     tfUser  = field("Username",  160);
        PasswordField pfPass  = passField("Password");
        pfPass.setPrefWidth(160);

        ComboBox<String> cbRole = new ComboBox<>();
        cbRole.getItems().addAll("USER","ADMIN");
        cbRole.setValue("USER");
        cbRole.setStyle(inputStyle());

        Label  errLbl    = errorLabel();
        Button btnCreate  = primaryBtn("Create User");
        Button btnEnable  = successBtn("Enable");
        Button btnDisable = dangerBtn("Disable");
        Button btnRefresh = mutedBtn("Refresh");

        btnCreate.setOnAction(e -> {
            errLbl.setText("");
            if (tfUser.getText().isBlank() || pfPass.getText().isBlank()) {
                errLbl.setText("Username and password are required."); return;
            }
            try {
                AuthService.createUser(tfUser.getText().trim(),
                    pfPass.getText(), cbRole.getValue());
                tfUser.clear(); pfPass.clear();
                refresh();
            } catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnEnable.setOnAction(e -> {
            AppUser sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a user first."); return; }
            try { AuthService.toggleUser(sel.getId(), true); refresh(); }
            catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnDisable.setOnAction(e -> {
            AppUser sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { errLbl.setText("Select a user first."); return; }
            try { AuthService.toggleUser(sel.getId(), false); refresh(); }
            catch (SQLException ex) { errLbl.setText(ex.getMessage()); }
        });

        btnRefresh.setOnAction(ev -> refresh());

        HBox formRow = new HBox(8, tfUser, pfPass, cbRole,
                                btnCreate, btnEnable, btnDisable, btnRefresh);

        VBox formPanel = new VBox(12,
            pageTitle("Admin — User Management"),
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
        data.clear();
        try (ResultSet rs = DBConnection.get().createStatement()
                .executeQuery("SELECT user_id,username,role,active FROM app_user ORDER BY user_id")) {
            while (rs.next())
                data.add(new AppUser(rs.getInt("user_id"), rs.getString("username"),
                    rs.getString("role"), rs.getBoolean("active")));
        } catch (SQLException e) { showAlert(Alert.AlertType.ERROR, "Error", e.getMessage()); }
    }
}
