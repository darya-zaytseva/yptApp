package org.example.yptapp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxml, String title) {
        try {
            var resource = SceneManager.class.getResource("/org/example/yptapp/" + fxml);
            if (resource == null) {
                showError("Не удалось найти файл интерфейса: " + fxml);
                return;
            }
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("YPT - " + title);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            showError("Ошибка загрузки интерфейса: " + fxml + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}