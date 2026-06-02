package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.yptapp.dao.GroupDAO;
import org.example.yptapp.dao.StudentDAO;
import org.example.yptapp.model.Group;
import org.example.yptapp.model.Student;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class StudentsController implements Initializable {
    @FXML private TableView<Student> table;
    @FXML private TableColumn<Student, String> colName, colGroup, colPhone;
    @FXML private TextField firstName, lastName, middleName, phone, email;
    @FXML private ComboBox<Group> groupCombo;
    @FXML private DatePicker birthDate;

    private StudentDAO dao = new StudentDAO();
    private GroupDAO groupDao = new GroupDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGroup.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        try { groupCombo.setItems(FXCollections.observableArrayList(groupDao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> { if (val != null) fillForm(val); });
        load();
    }

    private void load() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    private void fillForm(Student s) {
        firstName.setText(s.getFirstName());
        lastName.setText(s.getLastName());
        middleName.setText(s.getMiddleName());
        phone.setText(s.getPhone());
        email.setText(s.getEmail());
        birthDate.setValue(s.getBirthDate());
        groupCombo.getItems().stream().filter(g -> g.getId() == s.getGroupId()).findFirst().ifPresent(groupCombo::setValue);
    }

    @FXML private void add() {
        Student s = getFromForm(); if (s == null) return;
        try { dao.add(s); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void update() {
        Student sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        Student s = getFromForm(); if (s == null) return;
        s.setId(sel.getId());
        try { dao.update(s); load(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void delete() {
        Student sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        try { dao.delete(sel.getId()); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private Student getFromForm() {
        if (lastName.getText().isEmpty() || firstName.getText().isEmpty()) { showAlert("Фамилия и имя обязательны"); return null; }
        Student s = new Student();
        s.setLastName(lastName.getText());
        s.setFirstName(firstName.getText());
        s.setMiddleName(middleName.getText());
        s.setPhone(phone.getText());
        s.setEmail(email.getText());
        s.setBirthDate(birthDate.getValue());
        Group g = groupCombo.getValue();
        s.setGroupId(g != null ? g.getId() : 0);
        return s;
    }

    private void clear() { firstName.clear(); lastName.clear(); middleName.clear(); phone.clear(); email.clear(); birthDate.setValue(null); groupCombo.setValue(null); }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}