package com.example.vehicleidentificationsystem.util;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class UIEffects {
    public static void fadeIn(Node node, double seconds) {
        node.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.seconds(seconds), node);
        ft.setFromValue(0.0); ft.setToValue(1.0); ft.play();
    }

    public static void applyFadeLoop(Node node) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1.4), node);
        fade.setFromValue(1.0); fade.setToValue(0.35);
        fade.setAutoReverse(true);
        fade.setCycleCount(FadeTransition.INDEFINITE);
        fade.play();
    }
}