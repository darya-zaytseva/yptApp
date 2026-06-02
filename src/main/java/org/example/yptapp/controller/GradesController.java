package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.yptapp.dao.GradeDAO;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.model.Grade;
import org.example.yptapp.model.Student;
import org.example.yptapp.model.Subject;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class GradesController implements Initializable {
    @FXML private TableView<Grade> table;
    @FXML private TableColumn<Grade, String> colStudent, colSubject, colType;
    @FXML private TableColumn<Grade, Double> colGrade;
    @FXML private TableColumn<Grade, String> colDate;
    @FXML private ComboBox<Student> studentCombo;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField gradeField, commentField;
    @FXML private DatePicker datePicker;

    private GradeDAO dao = new GradeDAO();
    private StudentDAO studentDao = new StudentDAO();
    private SubjectDAO subjectDao = new SubjectDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        typeCombo.setItems(FXCollections.observableArrayList("exam", "test", "homework", "project"));
        typeCombo.setValue("test");
        datePicker.setValue(LocalDate.now());

        try {
            studentCombo.setItems(FXCollections.observableArrayList(studentDao.getAll()));
            subjectCombo.setItems(FXCollections.observableArrayList(subjectDao.getAll()));
        } catch (SQLException e) { e.printStackTrace(); }

        load();
    }

    private void load() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void add() {
        Grade g = getFromForm(); if (g == null) return;
        try { dao.add(g); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void update() {
        Grade sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        Grade g = getFromForm(); if (g == null) return;
        g.setId(sel.getId());
        try { dao.update(g); load(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void delete() {
        Grade sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        try { dao.delete(sel.getId()); load(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private Grade getFromForm() {
        if (gradeField.getText().isEmpty()) { showAlert("Введите оценку"); return null; }
        Grade g = new Grade();
        Student s = studentCombo.getValue();
        Subject sub = subjectCombo.getValue();
        if (s == null || sub == null) { showAlert("Выберите студента и предмет"); return null; }
        g.setStudentId(s.getId());
        g.setSubjectId(sub.getId());
        try { g.setGrade(Double.parseDouble(gradeField.getText())); }
        catch (NumberFormatException e) { showAlert("Оценка должна быть числом"); return null; }
        g.setType(typeCombo.getValue());
        g.setDate(datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now());
        g.setComment(commentField.getText());
        return g;
    }

    private void clear() { gradeField.clear(); commentField.clear(); datePicker.setValue(LocalDate.now()); }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}