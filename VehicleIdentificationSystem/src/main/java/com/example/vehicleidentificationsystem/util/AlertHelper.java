package com.example.vehicleidentificationsystem.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertHelper {
    public static void showInfo(String title, String msg) {
        Alert a = new Alert(AlertType.INFORMATION); a.setTitle(title);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    public static void showError(String title, String msg) {
        Alert a = new Alert(AlertType.ERROR); a.setTitle(title);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    public static boolean showConfirm(String title, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION); a.setTitle(title);
        a.setHeaderText(null); a.setContentText(msg);
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get() == ButtonType.OK;
    }
    public static void showWarning(String title, String msg) {
        Alert a = new Alert(AlertType.WARNING); a.setTitle(title);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}