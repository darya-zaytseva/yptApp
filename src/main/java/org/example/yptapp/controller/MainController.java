package org.example.yptapp.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.example.yptapp.dao.GroupDAO;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private Label studentCountLabel;
    @FXML private Label subjectCountLabel;
    @FXML private Label groupCountLabel;

    private StudentDAO studentDao = new StudentDAO();
    private SubjectDAO subjectDao = new SubjectDAO();
    private GroupDAO groupDao = new GroupDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadStats();
    }

    private void loadStats() {
        try {
            int students = studentDao.getAll().size();
            int subjects = subjectDao.getAll().size();
            int groups = groupDao.getAll().size();

            Platform.runLater(() -> {
                if (studentCountLabel != null) studentCountLabel.setText(String.valueOf(students));
                if (subjectCountLabel != null) subjectCountLabel.setText(String.valueOf(subjects));
                if (groupCountLabel != null) groupCountLabel.setText(String.valueOf(groups));
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void openGroups() { SceneManager.switchScene("groups.fxml", "Группы"); }
    @FXML private void openStudents() { SceneManager.switchScene("students.fxml", "Студенты"); }
    @FXML private void openSubjects() { SceneManager.switchScene("subjects.fxml", "Предметы"); }
    @FXML private void openSchedule() { SceneManager.switchScene("schedule.fxml", "Расписание"); }
    @FXML private void openCalendar() { SceneManager.switchScene("calendar.fxml", "Календарь"); }
    @FXML private void openGrades() { SceneManager.switchScene("grades.fxml", "Оценки"); }
    @FXML private void openMaterials() { SceneManager.switchScene("materials.fxml", "Учебники"); }
    @FXML private void openFriends() { SceneManager.switchScene("friends.fxml", "Друзья"); }
    @FXML private void logout() {
        org.example.yptapp.model.UserSession.clear();
        SceneManager.switchScene("login.fxml", "Авторизация");
    }
}