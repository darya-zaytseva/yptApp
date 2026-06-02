package org.example.yptapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.yptapp.util.SceneManager;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        SceneManager.setStage(stage);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/yptapp/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 650);

        stage.setTitle("YPT - Учебная группа");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}