module com.example.vehicleidentificationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;

    opens com.example.vehicleidentificationsystem to javafx.fxml;
    opens com.example.vehicleidentificationsystem.controller to javafx.fxml;
    opens com.example.vehicleidentificationsystem.model to javafx.base;
    exports com.example.vehicleidentificationsystem;
}