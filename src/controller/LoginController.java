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
import service.AuthService;

import java.sql.SQLException;
import static controller.UIHelper.*;

public class LoginController {

    private final Stage stage;
    public LoginController(Stage stage) { this.stage = stage; }

    public Scene buildScene() {
        // ── root ──────────────────────────────────────────────
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:" + HDR_BG + ";");

        // ── left branding panel ───────────────────────────────
        VBox brand = new VBox(16);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.setPadding(new Insets(60, 50, 60, 60));
        brand.setMinWidth(380);
        brand.setStyle("-fx-background-color:" + NAVY + ";");

        Label logo = new Label("VIS");
        logo.setFont(Font.font("System", FontWeight.BOLD, 52));
        logo.setTextFill(Color.web(GOLD));
        applyBtnShadow(logo);

        Label brandTitle = new Label("Vehicle\nIdentification\nSystem");
        brandTitle.setFont(Font.font("System", FontWeight.BOLD, 26));
        brandTitle.setTextFill(Color.web(HDR_TEXT));
        brandTitle.setLineSpacing(4);

        Label brandSub = new Label("Secure vehicle registry for law enforcement,\n" +
                                   "insurance, and public services.");
        brandSub.setFont(Font.font("System", 13));
        brandSub.setTextFill(Color.web(HDR_MUTED));
        brandSub.setWrapText(true);
        brandSub.setLineSpacing(3);

        Separator divider = new Separator();
        divider.setStyle("-fx-background-color:#2d4159;");
        divider.setMaxWidth(60);

        Label uni = new Label("Limkokwing University of Creative Technology");
        uni.setFont(Font.font("System", 11));
        uni.setTextFill(Color.web(HDR_MUTED));
        uni.setWrapText(true);

        brand.getChildren().addAll(logo, brandTitle, divider, brandSub, uni);
        root.setLeft(brand);

        // ── right login panel ─────────────────────────────────
        VBox loginPanel = new VBox(0);
        loginPanel.setAlignment(Pos.CENTER);
        loginPanel.setStyle("-fx-background-color:" + BG + ";");

        VBox card = new VBox(20);
        card.setMaxWidth(400);
        card.setMinWidth(340);
        card.setPadding(new Insets(40));
        card.setStyle(cardStyle());
        applyCardShadow(card);

        Label cardTitle = new Label("Welcome back");
        cardTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        cardTitle.setTextFill(Color.web(TEXT));

        Label cardSub = new Label("Sign in to your teller account");
        cardSub.setFont(Font.font("System", 13));
        cardSub.setTextFill(Color.web(TEXT_MUTED));

        TextField     tfUser = field("Enter your username");
        tfUser.setMaxWidth(Double.MAX_VALUE);
        PasswordField pfPass = passField("Enter your password");
        Label         lblErr = errorLabel();

        Button btnLogin = primaryBtn("SIGN IN");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        applyFadeTransition(btnLogin);

        btnLogin.setOnAction(e -> {
            lblErr.setText("");
            String u = tfUser.getText().trim();
            String p = pfPass.getText();
            if (u.isEmpty() || p.isEmpty()) {
                lblErr.setText("Please enter both username and password.");
                return;
            }
            try {
                AppUser user = AuthService.login(u, p);
                if (user == null) {
                    lblErr.setText("Invalid credentials or account is disabled.");
                    return;
                }
                new MainController(stage, user).show();
            } catch (SQLException ex) {
                lblErr.setText("Connection error: " + ex.getMessage());
            }
        });

        pfPass.setOnAction(e -> btnLogin.fire());

        card.getChildren().addAll(
            cardTitle, cardSub, separator(),
            fieldRow("Username", tfUser),
            fieldRow("Password", pfPass),
            btnLogin, lblErr
        );

        StackPane centerWrap = new StackPane(card);
        centerWrap.setPadding(new Insets(60));
        loginPanel.getChildren().add(centerWrap);
        root.setCenter(loginPanel);

        return new Scene(root, 900, 580);
    }
}
