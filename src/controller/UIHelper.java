package controller;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Central design system — all colours, typography, and component
 * factories are defined here so every screen stays consistent.
 */
public final class UIHelper {

    // ── Colour palette ────────────────────────────────────────
    // Page & surface
    public static final String BG           = "#f0f2f5";
    public static final String CARD         = "#ffffff";
    public static final String CARD_ALT     = "#fafbfc";
    public static final String BORDER       = "#e2e6ea";

    // Brand — deep navy + refined gold
    public static final String NAVY         = "#1c2b3a";
    public static final String NAVY_LIGHT   = "#243548";
    public static final String GOLD         = "#b5892a";
    public static final String GOLD_HOVER   = "#8c6820";
    public static final String GOLD_TINT    = "#fdf3dc";
    public static final String GOLD_BORDER  = "#d4a843";

    // Semantic colours
    public static final String SUCCESS      = "#1a6640";
    public static final String SUCCESS_TINT = "#d4edda";
    public static final String DANGER       = "#9b2c2c";
    public static final String DANGER_TINT  = "#fde8e8";
    public static final String WARNING      = "#92600a";
    public static final String WARNING_TINT = "#fef3c7";
    public static final String INFO         = "#1a4f7a";
    public static final String INFO_TINT    = "#dbeafe";

    // Text
    public static final String TEXT         = "#1c2b3a";
    public static final String TEXT_SEC     = "#4a5568";
    public static final String TEXT_MUTED   = "#8896a8";

    // Header (dark navy bar)
    public static final String HDR_BG       = "#1c2b3a";
    public static final String HDR_BORDER   = "#2d4159";
    public static final String HDR_TEXT     = "#f0e6cc";
    public static final String HDR_MUTED    = "#7a8fa6";

    // Aliases kept so existing code compiles
    public static final String ACCENT        = GOLD;
    public static final String GREEN         = SUCCESS;
    public static final String RED           = DANGER;
    public static final String MUTED         = TEXT_MUTED;
    public static final String HEADER_BG     = HDR_BG;
    public static final String HEADER_TEXT   = HDR_TEXT;
    public static final String HEADER_MUTED  = HDR_MUTED;
    public static final String HEADER_BORDER = HDR_BORDER;

    private UIHelper() {}

    // ── Input styles ──────────────────────────────────────────
    public static String inputStyle() {
        return "-fx-background-color:#ffffff;" +
               "-fx-text-fill:" + TEXT + ";" +
               "-fx-border-color:" + BORDER + ";" +
               "-fx-border-radius:6;-fx-background-radius:6;" +
               "-fx-prompt-text-fill:" + TEXT_MUTED + ";" +
               "-fx-padding:8 12 8 12;" +
               "-fx-font-size:13;";
    }

    private static String inputFocusStyle() {
        return "-fx-background-color:#ffffff;" +
               "-fx-text-fill:" + TEXT + ";" +
               "-fx-border-color:" + GOLD_BORDER + ";" +
               "-fx-border-radius:6;-fx-background-radius:6;" +
               "-fx-prompt-text-fill:" + TEXT_MUTED + ";" +
               "-fx-padding:8 12 8 12;" +
               "-fx-font-size:13;";
    }

    public static TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(inputStyle());
        tf.focusedProperty().addListener((o, was, is) ->
            tf.setStyle(is ? inputFocusStyle() : inputStyle()));
        return tf;
    }

    public static TextField field(String prompt, double width) {
        TextField tf = field(prompt);
        tf.setPrefWidth(width);
        return tf;
    }

    public static PasswordField passField(String prompt) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(prompt);
        pf.setMaxWidth(Double.MAX_VALUE);
        pf.setStyle(inputStyle());
        pf.focusedProperty().addListener((o, was, is) ->
            pf.setStyle(is ? inputFocusStyle() : inputStyle()));
        return pf;
    }

    public static TextArea area(String prompt) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(2);
        ta.setWrapText(true);
        ta.setStyle(inputStyle());
        return ta;
    }

    public static <T> ComboBox<T> combo(String prompt) {
        ComboBox<T> cb = new ComboBox<>();
        cb.setPromptText(prompt);
        cb.setStyle(inputStyle());
        cb.setMaxWidth(Double.MAX_VALUE);
        return cb;
    }

    // ── Buttons ───────────────────────────────────────────────
    public static Button primaryBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + GOLD + ";" +
            "-fx-text-fill:#ffffff;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;");
        b.setOnMouseEntered(e -> b.setStyle(
            "-fx-background-color:" + GOLD_HOVER + ";" +
            "-fx-text-fill:#ffffff;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;"));
        b.setOnMouseExited(e -> b.setStyle(
            "-fx-background-color:" + GOLD + ";" +
            "-fx-text-fill:#ffffff;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;"));
        applyBtnShadow(b);
        return b;
    }

    public static Button secondaryBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + GOLD_TINT + ";" +
            "-fx-text-fill:" + GOLD_HOVER + ";" +
            "-fx-border-color:" + GOLD_BORDER + ";" +
            "-fx-border-radius:6;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;");
        return b;
    }

    public static Button dangerBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + DANGER_TINT + ";" +
            "-fx-text-fill:" + DANGER + ";" +
            "-fx-border-color:" + DANGER + ";" +
            "-fx-border-radius:6;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;");
        return b;
    }

    public static Button mutedBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:#ffffff;" +
            "-fx-text-fill:" + TEXT_SEC + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:6;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;");
        return b;
    }

    public static Button successBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color:" + SUCCESS_TINT + ";" +
            "-fx-text-fill:" + SUCCESS + ";" +
            "-fx-border-color:" + SUCCESS + ";" +
            "-fx-border-radius:6;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:6;" +
            "-fx-cursor:hand;" +
            "-fx-padding:9 22 9 22;" +
            "-fx-font-size:13;");
        return b;
    }

    /** Continuous fade on login button — required by assignment. */
    public static void applyFadeTransition(Button btn) {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.6), btn);
        ft.setFromValue(1.0);
        ft.setToValue(0.5);
        ft.setAutoReverse(true);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.play();
    }

    /** Gold DropShadow on primary buttons — required by assignment. */
    public static void applyBtnShadow(javafx.scene.Node node) {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#b5892a50"));
        ds.setRadius(10);
        ds.setOffsetY(3);
        ds.setSpread(0.05);
        node.setEffect(ds);
    }

    /** Subtle elevation shadow for cards and stat panels. */
    public static void applyCardShadow(javafx.scene.Node node) {
        DropShadow ds = new DropShadow();
        ds.setColor(Color.web("#00000014"));
        ds.setRadius(14);
        ds.setOffsetY(3);
        ds.setSpread(0.02);
        node.setEffect(ds);
    }

    // Keep old name so existing callers compile
    public static void applyDropShadow(javafx.scene.Node node) { applyBtnShadow(node); }

    // ── Labels ────────────────────────────────────────────────
    public static Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 13));
        l.setTextFill(Color.web(TEXT));
        return l;
    }

    public static Label pageTitle(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 15));
        l.setTextFill(Color.web(TEXT));
        return l;
    }

    public static Label muted(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.web(TEXT_MUTED));
        l.setFont(Font.font("System", 12));
        return l;
    }

    public static Label badge(String text, String bg, String fg) {
        Label l = new Label("  " + text + "  ");
        l.setFont(Font.font("System", FontWeight.BOLD, 11));
        l.setTextFill(Color.web(fg));
        l.setStyle("-fx-background-color:" + bg + ";-fx-background-radius:4;");
        return l;
    }

    public static Label errorLabel() {
        Label l = new Label();
        l.setTextFill(Color.web(DANGER));
        l.setFont(Font.font("System", 12));
        l.setWrapText(true);
        return l;
    }

    public static Separator separator() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:" + BORDER + ";");
        return s;
    }

    public static VBox fieldRow(String labelText, javafx.scene.Node input) {
        Label lbl = new Label(labelText);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 11));
        lbl.setTextFill(Color.web(TEXT_SEC));
        VBox row = new VBox(5, lbl, input);
        row.setMaxWidth(Double.MAX_VALUE);
        return row;
    }

    // ── Panel / layout helpers ────────────────────────────────
    public static String cardStyle() {
        return "-fx-background-color:" + CARD + ";" +
               "-fx-background-radius:10;" +
               "-fx-border-color:" + BORDER + ";" +
               "-fx-border-radius:10;";
    }

    public static String formPanelStyle() {
        return "-fx-background-color:" + CARD + ";" +
               "-fx-background-radius:8;" +
               "-fx-border-color:" + BORDER + ";" +
               "-fx-border-radius:8;";
    }

    /** Styled TableView — call once after columns are added. */
    public static void styleTable(TableView<?> table) {
        table.setStyle(
            "-fx-background-color:" + CARD + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public static void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
