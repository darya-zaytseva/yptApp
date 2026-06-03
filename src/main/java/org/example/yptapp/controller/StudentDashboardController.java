package org.example.yptapp.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.example.yptapp.dao.*;
import org.example.yptapp.model.*;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {
    @FXML private Label welcomeLabel;
    @FXML private VBox scheduleBox;
    @FXML private VBox progressBox;
    @FXML private ListView<String> materialsList;
    @FXML private Label timerLabel;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private Button timerButton;
    @FXML private VBox achievementsBox;
    @FXML private Label notifBadge;
    @FXML private Label levelLabel;
    @FXML private Label goalsLabel;
    @FXML private Label studyStatsLabel;

    private UserSession session;
    private StudySessionDAO studyDao = new StudySessionDAO();
    private ProgressDAO progressDao = new ProgressDAO();
    private MaterialDAO materialDao = new MaterialDAO();
    private ScheduleDAO scheduleDao = new ScheduleDAO();
    private SubjectDAO subjectDao = new SubjectDAO();
    private AchievementDAO achievementDao = new AchievementDAO();
    private NotificationDAO notifDao = new NotificationDAO();
    private int currentSessionId = -1;
    private boolean timerRunning = false;
    private Timeline timerTimeline;
    private int elapsedSeconds = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        session = UserSession.getInstance();
        welcomeLabel.setText("Привет, " + session.getFullName() + "!");

        levelLabel.setText("Уровень: " + (session.getLevel() != null ? session.getLevel() : "beginner"));
        goalsLabel.setText("Цели: " + (session.getGoals() != null ? session.getGoals() : "Не указаны"));

        loadSchedule();
        loadProgress();
        loadMaterials();
        loadSubjects();
        loadAchievements();
        loadNotifications();
        loadStudyStats();
    }

    private void loadSchedule() {
        scheduleBox.getChildren().clear();
        int today = LocalDate.now().getDayOfWeek().getValue();
        try {
            var list = scheduleDao.getAll();
            for (Schedule s : list) {
                if (s.getDayOfWeek() == today) {
                    Label l = new Label("• " + s.getSubjectName() + " (" + s.getPairNumber() + " пара, " + s.getRoom() + ")");
                    l.setStyle("-fx-text-fill: #37474f; -fx-font-size: 13px;");
                    scheduleBox.getChildren().add(l);
                }
            }
            if (scheduleBox.getChildren().isEmpty()) {
                scheduleBox.getChildren().add(new Label("Сегодня занятий нет"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadProgress() {
        progressBox.getChildren().clear();
        if (session.getStudentId() == null) {
            progressBox.getChildren().add(new Label("Прогресс недоступен: не найдена связь со студентом"));
            return;
        }
        try {
            progressDao.updateProgressForAllSubjects(session.getStudentId());
            var list = progressDao.getByStudent(session.getStudentId());
            for (Progress p : list) {
                VBox card = createProgressCard(p);
                progressBox.getChildren().add(card);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private VBox createProgressCard(Progress p) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label title = new Label(p.getSubjectName());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1a237e;");

        HBox stats = new HBox(20);
        double percent = p.getCompletionPercent();

        // Используем ProgressIndicator вместо Arc для корректного отображения
        javafx.scene.control.ProgressIndicator pi = new javafx.scene.control.ProgressIndicator(percent / 100.0);
        pi.setPrefSize(50, 50);
        pi.setStyle("-fx-progress-color: #00838f;");

        Label pct = new Label(String.format("%.0f%%", percent));
        pct.setStyle("-fx-font-weight: bold; -fx-text-fill: #00838f;");

        VBox info = new VBox(3);
        info.getChildren().addAll(
                new Label("Часов: " + p.getTotalHours()) {{ setStyle("-fx-text-fill: #546e7a; -fx-font-size: 12px;"); }},
                new Label("Заданий: " + p.getCompletedTasks() + "/" + p.getTotalTasks()) {{ setStyle("-fx-text-fill: #546e7a; -fx-font-size: 12px;"); }},
                new Label("Средний балл: " + String.format("%.1f", p.getAverageScore())) {{ setStyle("-fx-text-fill: #546e7a; -fx-font-size: 12px;"); }}
        );

        stats.getChildren().addAll(pi, info);
        card.getChildren().addAll(title, stats);
        return card;
    }

    private void loadMaterials() {
        try {
            var list = materialDao.getAll();
            materialsList.setItems(FXCollections.observableArrayList(
                    list.stream().map(m -> m.getSubjectName() + ": " + m.getTitle()).toList()
            ));
            // Добавляем обработчик двойного клика для открытия материала
            materialsList.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    int idx = materialsList.getSelectionModel().getSelectedIndex();
                    if (idx >= 0) {
                        try {
                            var materials = materialDao.getAll();
                            if (idx < materials.size()) {
                                Material m = materials.get(idx);
                                java.awt.Desktop.getDesktop().open(new java.io.File(m.getFilePath()));
                            }
                        } catch (Exception e) {
                            new Alert(Alert.AlertType.ERROR, "Не удалось открыть файл: " + e.getMessage()).showAndWait();
                        }
                    }
                }
            });
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadSubjects() {
        try {
            subjectCombo.setItems(FXCollections.observableArrayList(subjectDao.getAll()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadAchievements() {
        if (session.getStudentId() == null) return;
        achievementsBox.getChildren().clear();
        try {
            achievementDao.checkAndAward(session.getStudentId(), session.getUserId());
            var list = achievementDao.getUserAchievements(session.getUserId());
            for (Achievement a : list) {
                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                Label icon = new Label(a.getIcon());
                icon.setStyle("-fx-font-size: 24px;");
                VBox text = new VBox(2);
                Label title = new Label(a.getTitle());
                title.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a237e;");
                Label desc = new Label(a.getDescription());
                desc.setStyle("-fx-text-fill: #78909c; -fx-font-size: 11px;");
                text.getChildren().addAll(title, desc);
                row.getChildren().addAll(icon, text);
                achievementsBox.getChildren().add(row);
            }
            if (achievementsBox.getChildren().isEmpty()) {
                achievementsBox.getChildren().add(new Label("Пока нет достижений. Учитесь, чтобы получить первое!") {{ setStyle("-fx-text-fill: #90a4ae;"); }});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadNotifications() {
        try {
            int count = notifDao.getUnreadCount(session.getUserId());
            notifBadge.setText(count > 0 ? String.valueOf(count) : "");
            notifBadge.setVisible(count > 0);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadStudyStats() {
        if (session.getStudentId() == null) {
            studyStatsLabel.setText("Статистика недоступна");
            return;
        }
        try {
            var sessions = studyDao.getByStudent(session.getStudentId());
            int totalSeconds = sessions.stream().mapToInt(StudySession::getDurationSeconds).sum();
            int hours = totalSeconds / 3600;
            int mins = (totalSeconds % 3600) / 60;
            studyStatsLabel.setText(String.format("Всего учёбы: %dч %dмин (%d сессий)", hours, mins, sessions.size()));
        } catch (SQLException e) {
            studyStatsLabel.setText("Статистика недоступна");
        }
    }

    @FXML
    private void toggleTimer() {
        if (!timerRunning) {
            Subject s = subjectCombo.getValue();
            if (s == null || session.getStudentId() == null) {
                showAlert(session.getStudentId() == null ? "Ошибка: не найдена связь со студентом" : "Выберите предмет");
                return;
            }
            try {
                currentSessionId = studyDao.startSession(session.getStudentId(), s.getId());
                timerRunning = true;
                elapsedSeconds = 0;
                timerButton.setText("⏹ Стоп");
                timerButton.setStyle("-fx-background-color: #e53935; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 30;");
                startTimerUpdate();
            } catch (SQLException e) { e.printStackTrace(); }
        } else {
            try {
                studyDao.endSession(currentSessionId);
                timerRunning = false;
                if (timerTimeline != null) timerTimeline.stop();
                timerButton.setText("▶ Старт");
                timerButton.setStyle("-fx-background-color: #00838f; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 30;");
                timerLabel.setText("00:00:00");
                loadProgress();
                loadAchievements();
                loadStudyStats();

                Notification n = new Notification();
                n.setUserId(session.getUserId());
                n.setTitle("Сессия завершена");
                n.setMessage("Вы учились " + String.format("%02d:%02d:%02d", elapsedSeconds/3600, (elapsedSeconds%3600)/60, elapsedSeconds%60));
                notifDao.add(n);
                loadNotifications();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    private void startTimerUpdate() {
        timerTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            elapsedSeconds++;
            int h = elapsedSeconds / 3600;
            int m = (elapsedSeconds % 3600) / 60;
            int s = elapsedSeconds % 60;
            timerLabel.setText(String.format("%02d:%02d:%02d", h, m, s));
        }));
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.play();
    }

    @FXML
    private void openCalendar() {
        SceneManager.switchScene("calendar.fxml", "Календарь");
    }

    @FXML
    private void logout() {
        if (timerRunning) toggleTimer();
        UserSession.clear();
        SceneManager.switchScene("login.fxml", "Авторизация");
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}