package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.AppUser;

import static controller.UIHelper.*;

public class MainController {

    private final Stage   stage;
    private final AppUser user;

    public MainController(Stage stage, AppUser user) {
        this.stage = stage;
        this.user  = user;
    }

    public void show() {
        stage.setTitle("Vehicle Identification System");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + BG + ";");

        // ── menu bar ──────────────────────────────────────────
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color:" + NAVY_LIGHT + ";" +
                         "-fx-border-color:" + HDR_BORDER + ";" +
                         "-fx-border-width:0 0 1 0;" +
                         "-fx-padding:2 8 2 8;");

        Menu fileMenu  = new Menu("_File");
        Menu viewMenu  = new Menu("_View");
        Menu helpMenu  = new Menu("_Help");

        MenuItem exitItem    = new MenuItem("Exit");
        MenuItem refreshItem = new MenuItem("Refresh Current Tab");
        MenuItem aboutItem   = new MenuItem("About VIS");

        exitItem.setOnAction(e -> stage.close());
        aboutItem.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "About VIS",
            "Vehicle Identification System\n" +
            "Limkokwing University — OOP2 Project\n\n" +
            "Stack: JavaFX 21 | PostgreSQL | MVC Architecture\n" +
            "Version: 1.0.0"));

        fileMenu.getItems().add(exitItem);
        viewMenu.getItems().add(refreshItem);
        helpMenu.getItems().add(aboutItem);
        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);

        // style menu text white
        menuBar.lookupAll(".menu > .label").forEach(n ->
            ((javafx.scene.control.Label) n).setTextFill(Color.web(HDR_TEXT)));

        // ── header bar ────────────────────────────────────────
        Label vis = new Label("VIS");
        vis.setFont(Font.font("System", FontWeight.BOLD, 18));
        vis.setTextFill(Color.web(GOLD));

        Label appTitle = new Label("Vehicle Identification System");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 15));
        appTitle.setTextFill(Color.web(HDR_TEXT));

        HBox logoBox = new HBox(8, vis, appTitle);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label roleTag = new Label(user.getRole());
        roleTag.setFont(Font.font("System", FontWeight.BOLD, 10));
        roleTag.setTextFill(Color.web(GOLD));
        roleTag.setStyle("-fx-background-color:#2d4159;-fx-background-radius:4;" +
                         "-fx-padding:2 8 2 8;");

        Label userInfo = new Label("@" + user.getUsername());
        userInfo.setFont(Font.font("System", 13));
        userInfo.setTextFill(Color.web(HDR_MUTED));

        Button btnLogout = new Button("Log Out");
        btnLogout.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-text-fill:" + GOLD + ";" +
            "-fx-border-color:" + GOLD + ";" +
            "-fx-border-radius:5;-fx-background-radius:5;" +
            "-fx-font-weight:bold;-fx-cursor:hand;" +
            "-fx-padding:6 14 6 14;-fx-font-size:12;");
        btnLogout.setOnAction(e -> {
            stage.setScene(new LoginController(stage).buildScene());
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(10, roleTag, userInfo, btnLogout);
        userBox.setAlignment(Pos.CENTER_RIGHT);

        HBox headerBar = new HBox(logoBox, spacer, userBox);
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.setPadding(new Insets(14, 20, 14, 20));
        headerBar.setStyle("-fx-background-color:" + HDR_BG + ";");

        VBox topSection = new VBox(menuBar, headerBar);
        root.setTop(topSection);

        // ── tab pane ──────────────────────────────────────────
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setStyle("-fx-background-color:" + BG + ";-fx-tab-min-height:36;");

        tabs.getTabs().addAll(
            new DashboardController().buildTab(),
            new VehicleController().buildTab(),
            new CustomerController().buildTab(),
            new WorkshopController().buildTab(),
            new InsuranceController().buildTab(),
            new PoliceController().buildTab()
        );
        if ("ADMIN".equals(user.getRole()))
            tabs.getTabs().add(new AdminController().buildTab());

        root.setCenter(tabs);

        refreshItem.setOnAction(e -> {
            Tab sel = tabs.getSelectionModel().getSelectedItem();
            tabs.getSelectionModel().clearSelection();
            tabs.getSelectionModel().select(sel);
        });

        stage.setScene(new Scene(root, 1280, 800));
        stage.show();
    }
}
