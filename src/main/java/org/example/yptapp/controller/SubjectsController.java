package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.model.Subject;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SubjectsController implements Initializable {
    @FXML private TableView<Subject> table;
    @FXML private TableColumn<Subject, String> colName, colCode;
    @FXML private TableColumn<Subject, Integer> colHours;
    @FXML private TextField nameField, codeField;
    @FXML private Spinner<Integer> hoursSpinner;

    private SubjectDAO dao = new SubjectDAO();
    private Subject selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colHours.setCellValueFactory(new PropertyValueFactory<>("hours"));
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 1));

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            selected = val;
            if (val != null) {
                nameField.setText(val.getName());
                codeField.setText(val.getCode());
                hoursSpinner.getValueFactory().setValue(val.getHours());
            }
        });
        load();
    }

    private void load() {
        try { table.setItems(FXCollections.observableArrayList(dao.getAll())); }
        catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML private void add() {
        Subject s = new Subject(0, nameField.getText(), codeField.getText(), hoursSpinner.getValue());
        try { dao.add(s); load(); clear(); }
        catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void update() {
        if (selected == null) return;
        selected.setName(nameField.getText());
        selected.setCode(codeField.getText());
        selected.setHours(hoursSpinner.getValue());
        try { dao.update(selected); load(); }
        catch (SQLException e) { showAlert(e.getMessage()); }
    }

    @FXML private void delete() {
        if (selected == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Удалить предмет " + selected.getName() + "?");
        if (c.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try { dao.delete(selected.getId()); load(); clear(); }
            catch (SQLException e) { showAlert(e.getMessage()); }
        }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private void clear() { nameField.clear(); codeField.clear(); hoursSpinner.getValueFactory().setValue(1); selected = null; }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}