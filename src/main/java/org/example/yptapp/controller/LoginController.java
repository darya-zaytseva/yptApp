package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.service.AuthService;
import org.example.yptapp.util.SceneManager;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Заполните все поля");
            return;
        }

        if (AuthService.login(user, pass)) {
            UserSession session = UserSession.getInstance();
            if (session.isStudent()) {
                SceneManager.switchScene("student-dashboard.fxml", "Мой кабинет");
            } else {
                SceneManager.switchScene("main.fxml", "Главное меню");
            }
        } else {
            showAlert("Неверный логин или пароль");
        }
    }
    @FXML
    private void openRegister() {
        SceneManager.switchScene("register.fxml", "Регистрация");
    }
    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}