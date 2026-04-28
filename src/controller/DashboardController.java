package controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import service.DBConnection;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static controller.UIHelper.*;

public class DashboardController {

    // stat card labels — updated on refresh
    private Label numVehicles, numCustomers, numReports, numFines, numPolicies;
    private ProgressBar pbVehicles, pbInsurance, pbFines;
    private ProgressIndicator pi;

    public Tab buildTab() {
        Tab tab = new Tab("  Dashboard  ");

        // ── stat cards ────────────────────────────────────────
        numVehicles  = bigNum(); numCustomers = bigNum();
        numReports   = bigNum(); numFines     = bigNum();
        numPolicies  = bigNum();

        HBox statsRow = new HBox(16,
            statCard(numVehicles,  "Vehicles",      NAVY,    GOLD),
            statCard(numCustomers, "Customers",     SUCCESS, SUCCESS_TINT),
            statCard(numReports,   "Reports",       INFO,    INFO_TINT),
            statCard(numFines,     "Unpaid Fines",  DANGER,  DANGER_TINT),
            statCard(numPolicies,  "Policies",      WARNING, WARNING_TINT)
        );
        statsRow.setPadding(new Insets(0,0,4,0));

        // ── progress indicators ───────────────────────────────
        VBox progressSection = new VBox(10);
        progressSection.setPadding(new Insets(16));
        progressSection.setStyle(formPanelStyle());

        Label progressTitle = sectionLabel("System Overview");

        pbVehicles  = progressBar(NAVY);
        pbInsurance = progressBar(WARNING);
        pbFines     = progressBar(DANGER);
        pi = new ProgressIndicator(0);
        pi.setPrefSize(52, 52);
        pi.setStyle("-fx-progress-color:" + GOLD + ";");

        GridPane pbGrid = new GridPane();
        pbGrid.setHgap(12); pbGrid.setVgap(8);
        pbGrid.add(muted("Registered Vehicles"), 0, 0); pbGrid.add(pbVehicles,  1, 0);
        pbGrid.add(muted("Active Policies"),      0, 1); pbGrid.add(pbInsurance, 1, 1);
        pbGrid.add(muted("Unpaid Violations"),    0, 2); pbGrid.add(pbFines,     1, 2);

        HBox piRow = new HBox(20, pbGrid, pi);
        piRow.setAlignment(Pos.CENTER_LEFT);
        progressSection.getChildren().addAll(progressTitle, separator(), piRow);
        applyCardShadow(progressSection);

        // ── activity log ──────────────────────────────────────
        VBox logSection = new VBox(10);
        logSection.setPadding(new Insets(16));
        logSection.setStyle(formPanelStyle());
        applyCardShadow(logSection);

        Label logTitle = sectionLabel("Activity Log");

        List<String> items = buildLog();
        final int PAGE_SIZE = 5;
        int pages = (int) Math.ceil(items.size() / (double) PAGE_SIZE);

        VBox listBox = new VBox(4);
        listBox.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane scroll = new ScrollPane(listBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(195);
        scroll.setStyle("-fx-background-color:" + BG + ";" +
                        "-fx-border-color:" + BORDER + ";-fx-border-radius:6;");

        Pagination pagination = new Pagination(Math.max(pages,1), 0);
        pagination.setStyle("-fx-page-information-visible:false;");
        pagination.setPageFactory(pageIndex -> {
            listBox.getChildren().clear();
            int from = pageIndex * PAGE_SIZE;
            int to   = Math.min(from + PAGE_SIZE, items.size());
            for (int i = from; i < to; i++) {
                Label row = new Label("  " + (i+1) + ".  " + items.get(i));
                row.setFont(Font.font("System", 12));
                row.setTextFill(Color.web(TEXT_SEC));
                row.setMaxWidth(Double.MAX_VALUE);
                row.setPadding(new Insets(7, 12, 7, 12));
                row.setStyle((i % 2 == 0
                    ? "-fx-background-color:" + CARD_ALT + ";"
                    : "-fx-background-color:" + CARD + ";") +
                    "-fx-background-radius:5;");
                listBox.getChildren().add(row);
            }
            return listBox;
        });

        logSection.getChildren().addAll(logTitle, separator(), scroll, pagination);

        VBox layout = new VBox(18, statsRow, progressSection, logSection);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane outer = new ScrollPane(layout);
        outer.setFitToWidth(true);
        outer.setStyle("-fx-background-color:" + BG + ";");
        tab.setContent(outer);

        // ── refresh on tab selection ──────────────────────────
        tab.setOnSelectionChanged(e -> { if (tab.isSelected()) refresh(); });
        refresh();
        return tab;
    }

    private void refresh() {
        int v  = count("vehicle");
        int c  = count("customer");
        int r  = count("police_report");
        int f  = countWhere("violation",  "status='Unpaid'");
        int p  = countWhere("insurance",  "status='Active'");

        numVehicles.setText(String.valueOf(v));
        numCustomers.setText(String.valueOf(c));
        numReports.setText(String.valueOf(r));
        numFines.setText(String.valueOf(f));
        numPolicies.setText(String.valueOf(p));

        pbVehicles.setProgress(Math.min(v / 100.0, 1.0));
        pbInsurance.setProgress(Math.min(p / 50.0,  1.0));
        pbFines.setProgress(Math.min(f / 30.0,  1.0));
        pi.setProgress(pbVehicles.getProgress());
    }

    // ── helpers ───────────────────────────────────────────────
    private Label bigNum() {
        Label l = new Label("0");
        l.setFont(Font.font("System", FontWeight.BOLD, 34));
        return l;
    }

    private VBox statCard(Label numLabel, String label, String numColor, String bgTint) {
        numLabel.setTextFill(Color.web(numColor));
        Label lbl = new Label(label);
        lbl.setFont(Font.font("System", 12));
        lbl.setTextFill(Color.web(TEXT_MUTED));
        VBox card = new VBox(4, numLabel, lbl);
        card.setPadding(new Insets(18, 24, 18, 24));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(175);
        card.setStyle("-fx-background-color:" + bgTint + ";" +
                      "-fx-background-radius:10;" +
                      "-fx-border-color:" + BORDER + ";" +
                      "-fx-border-radius:10;");
        applyCardShadow(card);
        return card;
    }

    private ProgressBar progressBar(String color) {
        ProgressBar pb = new ProgressBar(0);
        pb.setPrefWidth(280);
        pb.setPrefHeight(10);
        pb.setStyle("-fx-accent:" + color + ";-fx-background-radius:5;-fx-background-color:" + BORDER + ";");
        return pb;
    }

    private int count(String table) {
        try (ResultSet rs = DBConnection.get().createStatement()
                .executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { return 0; }
    }

    private int countWhere(String table, String where) {
        try (ResultSet rs = DBConnection.get().createStatement()
                .executeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + where)) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { return 0; }
    }

    private List<String> buildLog() {
        List<String> log = new ArrayList<>();
        String[] dummy = {
            "System initialised successfully",
            "PostgreSQL connection established",
            "Schema verification passed",
            "Admin session opened",
            "Vehicle registry loaded",
            "Customer records loaded",
            "Insurance index refreshed",
            "Police report cache cleared",
            "Violation status check completed",
            "Backup procedure executed",
            "Audit log rotated",
            "Session timeout policy enforced",
            "New user registration available",
            "Stored procedures validated",
            "View integrity check passed",
            "Dashboard statistics refreshed",
            "Payment gateway status: OK",
            "Notification service running",
            "Report generation ready",
            "Health check: all systems normal"
        };
        for (String s : dummy) log.add(s);
        return log;
    }
}
