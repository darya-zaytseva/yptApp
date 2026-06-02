package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.example.yptapp.dao.FriendshipDAO;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.model.Friendship;
import org.example.yptapp.model.Student;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FriendsController implements Initializable {
    @FXML private TableView<Friendship> table;
    @FXML private TableColumn<Friendship, String> colName;
    @FXML private TableColumn<Friendship, String> colStatus;
    @FXML private ComboBox<Student> studentCombo;
    @FXML private HBox requestPanel;

    private FriendshipDAO dao = new FriendshipDAO();
    private StudentDAO studentDao = new StudentDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getOtherUserName(UserSession.getInstance().getUserId())
        ));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        try {
            studentCombo.setItems(FXCollections.observableArrayList(studentDao.getAll()));
            load();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void load() {
        try {
            int uid = UserSession.getInstance().getUserId();
            table.setItems(FXCollections.observableArrayList(dao.getByUser(uid)));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void sendRequest() {
        Student s = studentCombo.getValue();
        if (s == null || s.getUserId() == null) {
            showAlert("Выберите студента с привязанным пользователем");
            return;
        }
        try {
            dao.sendRequest(UserSession.getInstance().getUserId(), s.getUserId());
            load();
        } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML
    private void accept() {
        Friendship sel = table.getSelectionModel().getSelectedItem();
        if (sel == null || !"request".equals(sel.getStatus())) return;
        try {
            dao.accept(sel.getId());
            load();
        } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML
    private void delete() {
        Friendship sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            dao.delete(sel.getId());
            load();
        } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML
    private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}