package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.yptapp.dao.StudySessionDAO;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.model.StudySession;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LeaderboardController implements Initializable {
    @FXML private TableView<LeaderboardEntry> table;
    @FXML private TableColumn<LeaderboardEntry, String> colName;
    @FXML private TableColumn<LeaderboardEntry, String> colHours;
    @FXML private TableColumn<LeaderboardEntry, Integer> colSessions;

    private StudySessionDAO studyDao = new StudySessionDAO();
    private StudentDAO studentDao = new StudentDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colHours.setCellValueFactory(new PropertyValueFactory<>("hours"));
        colSessions.setCellValueFactory(new PropertyValueFactory<>("sessions"));
        load();
    }

    private void load() {
        try {
            var students = studentDao.getAll();
            var entries = FXCollections.<LeaderboardEntry>observableArrayList();
            for (var s : students) {
                var sessions = studyDao.getByStudent(s.getId());
                int totalSecs = sessions.stream().mapToInt(StudySession::getDurationSeconds).sum();
                int hours = totalSecs / 3600;
                int mins = (totalSecs % 3600) / 60;
                entries.add(new LeaderboardEntry(s.getFullName(), String.format("%dч %dмин", hours, mins), sessions.size()));
            }
            entries.sort((a, b) -> b.getSessions() - a.getSessions());
            table.setItems(entries);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    public static class LeaderboardEntry {
        private String name;
        private String hours;
        private int sessions;
        public LeaderboardEntry(String name, String hours, int sessions) {
            this.name = name; this.hours = hours; this.sessions = sessions;
        }
        public String getName() { return name; }
        public String getHours() { return hours; }
        public int getSessions() { return sessions; }
    }
}