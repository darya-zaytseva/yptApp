package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;
import org.example.yptapp.dao.GroupDAO;
import org.example.yptapp.model.Group;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.SceneManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class GroupsController implements Initializable {
    @FXML private TableView<Group> table;
    @FXML private TableColumn<Group, String> colName;
    @FXML private TableColumn<Group, String> colSpec;
    @FXML private TableColumn<Group, Integer> colYear;
    @FXML private TextField nameField, specField;
    @FXML private Spinner<Integer> yearSpinner;
    @FXML private ComboBox<String> privacyCombo;
    @FXML private Spinner<Integer> maxMembersSpinner;
    @FXML private TextArea descArea;

    private GroupDAO dao = new GroupDAO();
    private ObservableList<Group> data = FXCollections.observableArrayList();
    private Group selected;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        table.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #e0e0e0; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);");

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSpec.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        colYear.setCellValueFactory(new PropertyValueFactory<>("year"));

        colYear.setCellFactory(column -> new TableCell<Group, Integer>() {
            @Override
            protected void updateItem(Integer year, boolean empty) {
                super.updateItem(year, empty);
                if (empty || year == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label pill = new Label(year + " курс");
                    pill.setStyle("-fx-background-color: " + getYearColor(year) + "; -fx-text-fill: white; -fx-padding: 4 12; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    setGraphic(pill);
                    setText(null);
                }
            }
        });

        table.setRowFactory(tv -> new TableRow<Group>() {
            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setStyle("-fx-background-color: white;");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: #fafafa;");
                } else {
                    setStyle("-fx-background-color: white;");
                }
            }
        });

        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 1));
        maxMembersSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 200, 50));
        privacyCombo.setItems(FXCollections.observableArrayList("open", "closed", "invite"));
        privacyCombo.setValue("open");

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            selected = val;
            if (val != null) {
                nameField.setText(val.getName());
                specField.setText(val.getSpecialty());
                yearSpinner.getValueFactory().setValue(val.getYear());
                privacyCombo.setValue(val.getPrivacy());
                maxMembersSpinner.getValueFactory().setValue(val.getMaxMembers());
                descArea.setText(val.getDescription());
            }
        });
        load();
    }

    private String getYearColor(int year) {
        return switch (year) {
            case 1 -> "#5c6bc0";
            case 2 -> "#00838f";
            case 3 -> "#ff7043";
            case 4 -> "#26a69a";
            case 5 -> "#ffa726";
            default -> "#78909c";
        };
    }

    private void load() {
        try {
            data.setAll(dao.getAll());
            table.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML private void add() {
        if (!validate()) return;
        Group g = getFromForm();
        try { dao.add(g); load(); clear(); }
        catch (SQLException e) { showAlert("Ошибка: " + e.getMessage()); }
    }

    @FXML private void update() {
        if (selected == null) return;
        if (!validate()) return;
        selected.setName(nameField.getText());
        selected.setSpecialty(specField.getText());
        selected.setYear(yearSpinner.getValue());
        selected.setPrivacy(privacyCombo.getValue());
        selected.setMaxMembers(maxMembersSpinner.getValue());
        selected.setDescription(descArea.getText());
        try { dao.update(selected); load(); }
        catch (SQLException e) { showAlert("Ошибка: " + e.getMessage()); }
    }

    @FXML private void delete() {
        if (selected == null) return;
        Alert c = new Alert(Alert.AlertType.CONFIRMATION, "Удалить " + selected.getName() + "?");
        if (c.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try { dao.delete(selected.getId()); load(); clear(); }
            catch (SQLException e) { showAlert("Ошибка: " + e.getMessage()); }
        }
    }

    @FXML private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private Group getFromForm() {
        Group g = new Group();
        g.setName(nameField.getText().trim());
        g.setSpecialty(specField.getText().trim());
        g.setYear(yearSpinner.getValue());
        g.setPrivacy(privacyCombo.getValue());
        g.setMaxMembers(maxMembersSpinner.getValue());
        g.setDescription(descArea.getText());
        g.setCreatorId(UserSession.getInstance().getUserId());
        return g;
    }

    private boolean validate() {
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showAlert("Название группы обязательно"); return false;
        }
        if (specField.getText() == null || specField.getText().trim().isEmpty()) {
            showAlert("Специальность обязательна"); return false;
        }
        return true;
    }

    private void clear() {
        nameField.clear(); specField.clear(); yearSpinner.getValueFactory().setValue(1);
        privacyCombo.setValue("open"); maxMembersSpinner.getValueFactory().setValue(50);
        descArea.clear(); selected = null;
    }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
}