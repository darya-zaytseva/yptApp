package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.dao.TaskDAO;
import org.example.yptapp.model.Subject;
import org.example.yptapp.model.Task;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TasksController implements Initializable {
    @FXML private TableView<Task> table;
    @FXML private TableColumn<Task, String> colSubject, colTitle, colType;
    @FXML private TableColumn<Task, Integer> colMaxScore;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private TextField titleField, maxScoreField, deadlineField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> typeCombo;

    private TaskDAO dao = new TaskDAO();
    private SubjectDAO subjectDao = new SubjectDAO();
    private Task selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colMaxScore.setCellValueFactory(new PropertyValueFactory<>("maxScore"));

        typeCombo.setItems(FXCollections.observableArrayList("homework", "test", "project", "exam"));
        typeCombo.setValue("homework");

        try { subjectCombo.setItems(FXCollections.observableArrayList(subjectDao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            selected = val;
            if (val != null) fillForm(val);
        });
        load();
    }

    private void load() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    private void fillForm(Task t) {
        subjectCombo.getItems().stream().filter(s -> s.getId() == t.getSubjectId()).findFirst().ifPresent(subjectCombo::setValue);
        titleField.setText(t.getTitle());
        descArea.setText(t.getDescription());
        maxScoreField.setText(String.valueOf(t.getMaxScore()));
        typeCombo.setValue(t.getType());
        deadlineField.setText(t.getDeadline());
    }

    @FXML private void add() {
        Task t = getFromForm(); if (t == null) return;
        try { dao.add(t); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void update() {
        if (selected == null) return;
        Task t = getFromForm(); if (t == null) return;
        t.setId(selected.getId());
        try { dao.update(t); load(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void delete() {
        if (selected == null) return;
        try { dao.delete(selected.getId()); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private Task getFromForm() {
        if (titleField.getText().isEmpty()) { showAlert("Введите название задания"); return null; }
        Task t = new Task();
        Subject s = subjectCombo.getValue();
        if (s == null) { showAlert("Выберите предмет"); return null; }
        t.setSubjectId(s.getId());
        t.setTitle(titleField.getText());
        t.setDescription(descArea.getText());
        try { t.setMaxScore(Integer.parseInt(maxScoreField.getText())); } catch (Exception e) { t.setMaxScore(100); }
        t.setType(typeCombo.getValue());
        t.setDeadline(deadlineField.getText());
        return t;
    }

    private void clear() {
        titleField.clear(); descArea.clear(); maxScoreField.setText("100");
        typeCombo.setValue("homework"); deadlineField.clear(); selected = null;
    }

    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}