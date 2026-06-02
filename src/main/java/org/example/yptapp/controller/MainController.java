package org.example.yptapp.controller;

import javafx.fxml.FXML;
import org.example.yptapp.util.SceneManager;

public class MainController {
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