package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.yptapp.dao.GroupDAO;
import org.example.yptapp.dao.ScheduleDAO;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.model.Group;
import org.example.yptapp.model.Schedule;
import org.example.yptapp.model.Subject;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ScheduleController implements Initializable {
    @FXML private TableView<Schedule> table;
    @FXML private TableColumn<Schedule, String> colGroup, colSubject, colRoom;
    @FXML private TableColumn<Schedule, Integer> colDay, colPair;
    @FXML private ComboBox<Group> groupCombo;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private Spinner<Integer> daySpinner, pairSpinner;
    @FXML private TextField roomField;

    private ScheduleDAO dao = new ScheduleDAO();
    private GroupDAO groupDao = new GroupDAO();
    private SubjectDAO subjectDao = new SubjectDAO();
    private Schedule selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colGroup.setCellValueFactory(new PropertyValueFactory<>("groupName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colDay.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
        colPair.setCellValueFactory(new PropertyValueFactory<>("pairNumber"));
        colRoom.setCellValueFactory(new PropertyValueFactory<>("room"));

        daySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 7, 1));
        pairSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 8, 1));

        try {
            groupCombo.setItems(FXCollections.observableArrayList(groupDao.getAll()));
            subjectCombo.setItems(FXCollections.observableArrayList(subjectDao.getAll()));
        } catch (SQLException e) { e.printStackTrace(); }

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> { if (val != null) fillForm(val); });
        load();
    }

    private void load() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    private void fillForm(Schedule s) {
        groupCombo.getItems().stream().filter(g -> g.getId() == s.getGroupId()).findFirst().ifPresent(groupCombo::setValue);
        subjectCombo.getItems().stream().filter(sub -> sub.getId() == s.getSubjectId()).findFirst().ifPresent(subjectCombo::setValue);
        daySpinner.getValueFactory().setValue(s.getDayOfWeek());
        pairSpinner.getValueFactory().setValue(s.getPairNumber());
        roomField.setText(s.getRoom());
    }

    @FXML private void add() {
        Schedule s = getFromForm(); if (s == null) return;
        try { dao.add(s); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void update() {
        Schedule sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        Schedule s = getFromForm(); if (s == null) return;
        s.setId(sel.getId());
        try { dao.update(s); load(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void delete() {
        Schedule sel = table.getSelectionModel().getSelectedItem(); if (sel == null) return;
        try { dao.delete(sel.getId()); load(); clear(); } catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private Schedule getFromForm() {
        Schedule s = new Schedule();
        Group g = groupCombo.getValue();
        Subject sub = subjectCombo.getValue();
        if (g == null || sub == null) { showAlert("Выберите группу и предмет"); return null; }
        s.setGroupId(g.getId());
        s.setSubjectId(sub.getId());
        s.setDayOfWeek(daySpinner.getValue());
        s.setPairNumber(pairSpinner.getValue());
        s.setRoom(roomField.getText());
        return s;
    }

    private void clear() { groupCombo.setValue(null); subjectCombo.setValue(null); daySpinner.getValueFactory().setValue(1); pairSpinner.getValueFactory().setValue(1); roomField.clear(); }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}