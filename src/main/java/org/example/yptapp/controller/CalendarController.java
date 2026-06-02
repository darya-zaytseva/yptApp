package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.example.yptapp.dao.ScheduleDAO;
import org.example.yptapp.model.Schedule;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarController implements Initializable {
    @FXML private GridPane calendarGrid;
    @FXML private Label monthLabel;
    @FXML private HBox weekHeader;

    private ScheduleDAO scheduleDao = new ScheduleDAO();
    private LocalDate currentWeek = LocalDate.now();
    private Map<Integer, List<Schedule>> weekSchedule = new HashMap<>();

    private final String[] dayNames = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};
    private final Color[] subjectColors = {
            Color.web("#5c6bc0"), Color.web("#ff7043"), Color.web("#26a69a"),
            Color.web("#ab47bc"), Color.web("#ffa726"), Color.web("#42a5f5")
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadWeek();
    }

    private void loadWeek() {
        calendarGrid.getChildren().clear();
        weekHeader.getChildren().clear();

        LocalDate weekStart = currentWeek.with(java.time.DayOfWeek.MONDAY);
        monthLabel.setText(weekStart.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru"))));

        // Загрузка расписания
        try {
            var all = scheduleDao.getAll();
            weekSchedule.clear();
            for (Schedule s : all) {
                weekSchedule.computeIfAbsent(s.getDayOfWeek(), k -> new ArrayList<>()).add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Шапка дней
        for (int i = 0; i < 7; i++) {
            LocalDate day = weekStart.plusDays(i);
            boolean isToday = day.equals(LocalDate.now());

            VBox dayBox = new VBox(5);
            dayBox.setAlignment(Pos.CENTER);
            dayBox.setPadding(new Insets(10));
            dayBox.setPrefWidth(120);

            if (isToday) {
                dayBox.setStyle("-fx-background-color: #00838f; -fx-background-radius: 12;");
            } else {
                dayBox.setStyle("-fx-background-color: transparent;");
            }

            Label dayName = new Label(dayNames[i]);
            dayName.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (isToday ? "white" : "#78909c") + ";");

            Label dayNum = new Label(String.valueOf(day.getDayOfMonth()));
            dayNum.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + (isToday ? "white" : "#37474f") + ";");

            if (isToday) {
                Circle dot = new Circle(3, Color.web("#ff7043"));
                dayBox.getChildren().addAll(dayName, dayNum, dot);
            } else {
                dayBox.getChildren().addAll(dayName, dayNum);
            }

            weekHeader.getChildren().add(dayBox);
        }

        // Сетка времени (8:00 - 20:00)
        for (int hour = 8; hour <= 20; hour++) {
            Label timeLabel = new Label(String.format("%02d:00", hour));
            timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #90a4ae;");
            timeLabel.setPrefWidth(50);
            calendarGrid.add(timeLabel, 0, hour - 8);
        }

        // События по дням
        for (int day = 1; day <= 7; day++) {
            VBox dayColumn = new VBox(8);
            dayColumn.setPadding(new Insets(5));

            List<Schedule> events = weekSchedule.getOrDefault(day, new ArrayList<>());
            for (Schedule event : events) {
                VBox card = createEventCard(event, day - 1);
                dayColumn.getChildren().add(card);
            }

            calendarGrid.add(dayColumn, day, 0, 1, 13);
        }
    }

    private VBox createEventCard(Schedule event, int colorIndex) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: " + toHex(subjectColors[colorIndex % subjectColors.length]) +
                "20; -fx-background-radius: 10; -fx-border-color: " +
                toHex(subjectColors[colorIndex % subjectColors.length]) +
                "; -fx-border-radius: 10; -fx-border-width: 2;");

        Label subject = new Label(event.getSubjectName());
        subject.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: " +
                toHex(subjectColors[colorIndex % subjectColors.length]) + ";");

        Label details = new Label(event.getPairNumber() + " пара • " + event.getRoom());
        details.setStyle("-fx-font-size: 11px; -fx-text-fill: #546e7a;");

        Label group = new Label(event.getGroupName());
        group.setStyle("-fx-font-size: 11px; -fx-text-fill: #78909c;");

        card.getChildren().addAll(subject, details, group);
        return card;
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x",
                (int)(color.getRed() * 255),
                (int)(color.getGreen() * 255),
                (int)(color.getBlue() * 255));
    }

    @FXML private void prevWeek() {
        currentWeek = currentWeek.minusWeeks(1);
        loadWeek();
    }

    @FXML private void nextWeek() {
        currentWeek = currentWeek.plusWeeks(1);
        loadWeek();
    }

    @FXML private void back() {
        if (UserSession.getInstance().isStudent()) {
            SceneManager.switchScene("student-dashboard.fxml", "Мой кабинет");
        } else {
            SceneManager.switchScene("main.fxml", "Главное меню");
        }
    }
}