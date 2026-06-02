package org.example.yptapp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxml, String title) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource("/org/example/yptapp/" + fxml));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("YPT - " + title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}