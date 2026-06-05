package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.DBConnection;
import org.example.yptapp.util.SceneManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProfileController {
    @FXML private TextField fullNameField;
    @FXML private TextField locationField;
    @FXML private TextArea interestsArea;
    @FXML private TextArea goalsArea;
    @FXML private ComboBox<String> levelCombo;

    private UserSession session;

    @FXML
    public void initialize() {
        session = UserSession.getInstance();
        levelCombo.setItems(javafx.collections.FXCollections.observableArrayList("beginner", "intermediate", "advanced"));

        fullNameField.setText(session.getFullName());
        locationField.setText(session.getLocation() != null ? session.getLocation() : "");
        interestsArea.setText(session.getInterests() != null ? session.getInterests() : "");
        goalsArea.setText(session.getGoals() != null ? session.getGoals() : "");
        levelCombo.setValue(session.getLevel() != null ? session.getLevel() : "beginner");
    }

    @FXML
    private void save() {
        String sql = "UPDATE users SET full_name=?, location=?, interests=?, goals=?, level=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullNameField.getText());
            ps.setString(2, locationField.getText());
            ps.setString(3, interestsArea.getText());
            ps.setString(4, goalsArea.getText());
            ps.setString(5, levelCombo.getValue());
            ps.setInt(6, session.getUserId());
            ps.executeUpdate();

            session.setFullName(fullNameField.getText());
            session.setLocation(locationField.getText());
            session.setInterests(interestsArea.getText());
            session.setGoals(goalsArea.getText());
            session.setLevel(levelCombo.getValue());

            new Alert(Alert.AlertType.INFORMATION, "Профиль сохранён!").showAndWait();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка сохранения: " + e.getMessage()).showAndWait();
        }
    }

    @FXML private void back() {
        if (session.isStudent()) {
            SceneManager.switchScene("student-dashboard.fxml", "Мой кабинет");
        } else {
            SceneManager.switchScene("main.fxml", "Главное меню");
        }
    }
}