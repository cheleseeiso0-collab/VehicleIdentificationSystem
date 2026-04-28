package app;

import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Vehicle Identification System");
        stage.setMinWidth(900);
        stage.setMinHeight(580);
        stage.setScene(new LoginController(stage).buildScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
