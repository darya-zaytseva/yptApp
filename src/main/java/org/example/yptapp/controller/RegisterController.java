package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.yptapp.model.Student;
import org.example.yptapp.service.AuthService;
import org.example.yptapp.util.DBConnection;
import org.example.yptapp.util.SceneManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

    @FXML
    public void initialize() {
        roleCombo.setItems(javafx.collections.FXCollections.observableArrayList("student", "teacher"));
        roleCombo.setValue("student");
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String role = roleCombo.getValue();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
            showAlert("Заполните обязательные поля");
            return;
        }
        if (!password.equals(confirm)) {
            showAlert("Пароли не совпадают");
            return;
        }
        if (password.length() < 6) {
            showAlert("Пароль должен быть минимум 6 символов");
            return;
        }
        if (username.length() < 3) {
            showAlert("Логин должен быть минимум 3 символа");
            return;
        }

        try {
            if (role.equals("student")) {
                registerStudent(username, password, fullName);
            } else {
                registerTeacher(username, password, fullName);
            }
            showInfo("Регистрация успешна! Теперь войдите.");
            SceneManager.switchScene("login.fxml", "Авторизация");
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert("Такой логин уже занят");
            } else {
                showAlert("Ошибка регистрации: " + e.getMessage());
            }
        }
    }

    private void registerStudent(String username, String password, String fullName) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String hashedPassword = AuthService.hashPassword(password);

                // 1. Создаём пользователя
                String sqlUser = "INSERT INTO users (username, password_hash, role, full_name, level) VALUES (?, ?, 'student', ?, 'beginner')";
                int userId;
                try (PreparedStatement psUser = conn.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psUser.setString(1, username);
                    psUser.setString(2, hashedPassword);
                    psUser.setString(3, fullName);
                    psUser.executeUpdate();

                    try (ResultSet rs = psUser.getGeneratedKeys()) {
                        if (rs.next()) {
                            userId = rs.getInt(1);
                        } else {
                            throw new SQLException("Не удалось получить ID созданного пользователя");
                        }
                    }
                }

                // 2. Создаём студента
                Student s = new Student();
                s.setUserId(userId);
                s.setFirstName(firstNameField.getText().trim().isEmpty() ? fullName.split(" ")[0] : firstNameField.getText().trim());
                s.setLastName(lastNameField.getText().trim().isEmpty() ? fullName.split(" ")[0] : lastNameField.getText().trim());
                s.setMiddleName(middleNameField.getText().trim());
                s.setPhone(phoneField.getText().trim());
                s.setEmail(emailField.getText().trim());
                s.setGroupId(1);

                String sqlStudent = "INSERT INTO students (user_id, group_id, first_name, last_name, middle_name, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psStudent = conn.prepareStatement(sqlStudent)) {
                    psStudent.setInt(1, userId);
                    psStudent.setInt(2, s.getGroupId());
                    psStudent.setString(3, s.getFirstName());
                    psStudent.setString(4, s.getLastName());
                    psStudent.setString(5, s.getMiddleName());
                    psStudent.setString(6, s.getPhone());
                    psStudent.setString(7, s.getEmail());
                    psStudent.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private void registerTeacher(String username, String password, String fullName) throws SQLException {
        String hashedPassword = AuthService.hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash, role, full_name) VALUES (?, ?, 'teacher', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hashedPassword);
            ps.setString(3, fullName);
            ps.executeUpdate();
        }
    }

    @FXML
    private void backToLogin() {
        SceneManager.switchScene("login.fxml", "Авторизация");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).showAndWait();
    }
}