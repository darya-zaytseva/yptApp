package org.example.yptapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.yptapp.dao.NotificationDAO;
import org.example.yptapp.model.Notification;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {
    @FXML private ListView<VBox> notifList;
    @FXML private Label titleLabel;

    private NotificationDAO dao = new NotificationDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        titleLabel.setText("🔔 Уведомления — " + UserSession.getInstance().getFullName());
        load();
    }

    private void load() {
        try {
            var list = dao.getByUser(UserSession.getInstance().getUserId());
            notifList.getItems().clear();
            for (Notification n : list) {
                notifList.getItems().add(createCard(n));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private VBox createCard(Notification n) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: " + (n.isRead() ? "#f5f5f5" : "white") +
                "; -fx-background-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label title = new Label(n.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1a237e;");

        Label msg = new Label(n.getMessage());
        msg.setStyle("-fx-text-fill: #546e7a; -fx-font-size: 12px;");
        msg.setWrapText(true);

        Label date = new Label(n.getCreatedAt() != null ? n.getCreatedAt().toLocalDate().toString() : "");
        date.setStyle("-fx-text-fill: #90a4ae; -fx-font-size: 11px;");

        HBox actions = new HBox(10);
        if (!n.isRead()) {
            Button markBtn = new Button("Отметить прочитанным");
            markBtn.setStyle("-fx-background-color: #00838f; -fx-text-fill: white; -fx-background-radius: 8;");
            markBtn.setOnAction(e -> {
                try { dao.markAsRead(n.getId()); load(); } catch (SQLException ex) { ex.printStackTrace(); }
            });
            actions.getChildren().add(markBtn);
        }

        card.getChildren().addAll(title, msg, date, actions);
        return card;
    }

    @FXML private void back() {
        if (UserSession.getInstance().isStudent()) {
            SceneManager.switchScene("student-dashboard.fxml", "Мой кабинет");
        } else {
            SceneManager.switchScene("main.fxml", "Главное меню");
        }
    }
}