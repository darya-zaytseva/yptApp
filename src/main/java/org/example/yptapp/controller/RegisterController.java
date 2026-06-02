package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.model.Student;
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
        Connection conn = DBConnection.getConnection();
        conn.setAutoCommit(false);
        try {
            // 1. Создаём пользователя
            String sqlUser = "INSERT INTO users (username, password_hash, role, full_name, level) VALUES (?, SHA2(?, 256), 'student', ?, 'beginner')";
            PreparedStatement psUser = conn.prepareStatement(sqlUser, java.sql.Statement.RETURN_GENERATED_KEYS);
            psUser.setString(1, username);
            psUser.setString(2, password);
            psUser.setString(3, fullName);
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) userId = rs.getInt(1);

            // 2. Создаём студента
            Student s = new Student();
            s.setUserId(userId);
            s.setFirstName(firstNameField.getText().trim().isEmpty() ? fullName.split(" ")[0] : firstNameField.getText().trim());
            s.setLastName(lastNameField.getText().trim().isEmpty() ? fullName.split(" ")[0] : lastNameField.getText().trim());
            s.setMiddleName(middleNameField.getText().trim());
            s.setPhone(phoneField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setGroupId(1); // По умолчанию первая группа

            String sqlStudent = "INSERT INTO students (user_id, group_id, first_name, last_name, middle_name, phone, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psStudent = conn.prepareStatement(sqlStudent, java.sql.Statement.RETURN_GENERATED_KEYS);
            psStudent.setInt(1, userId);
            psStudent.setInt(2, s.getGroupId());
            psStudent.setString(3, s.getFirstName());
            psStudent.setString(4, s.getLastName());
            psStudent.setString(5, s.getMiddleName());
            psStudent.setString(6, s.getPhone());
            psStudent.setString(7, s.getEmail());
            psStudent.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private void registerTeacher(String username, String password, String fullName) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role, full_name) VALUES (?, SHA2(?, 256), 'teacher', ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
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