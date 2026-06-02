package org.example.yptapp.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.example.yptapp.dao.MaterialDAO;
import org.example.yptapp.dao.NotificationDAO;
import org.example.yptapp.dao.SubjectDAO;
import org.example.yptapp.model.Material;
import org.example.yptapp.model.Notification;
import org.example.yptapp.model.Subject;
import org.example.yptapp.model.UserSession;
import org.example.yptapp.util.SceneManager;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MaterialsController implements Initializable {
    @FXML private TableView<Material> table;
    @FXML private TableColumn<Material, String> colSubject;
    @FXML private TableColumn<Material, String> colTitle;
    @FXML private TableColumn<Material, String> colFile;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private TextField titleField;
    @FXML private Button uploadButton;
    @FXML private HBox adminPanel;

    private MaterialDAO dao = new MaterialDAO();
    private SubjectDAO subjectDao = new SubjectDAO();
    private NotificationDAO notifDao = new NotificationDAO();
    private File selectedFile;
    private static final Path BASE_DIR = Path.of(System.getProperty("user.home"), "YPT_Materials");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colSubject.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSubjectName()));
        colTitle.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
        colFile.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFileName()));

        boolean isAdmin = UserSession.getInstance().isAdmin() || UserSession.getInstance().isTeacher();
        adminPanel.setVisible(isAdmin);
        adminPanel.setManaged(isAdmin);

        try {
            subjectCombo.setItems(FXCollections.observableArrayList(subjectDao.getAll()));
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        try {
            table.setItems(FXCollections.observableArrayList(dao.getAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void chooseFile() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Выберите файл");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );
        selectedFile = fc.showOpenDialog(uploadButton.getScene().getWindow());
        if (selectedFile != null) {
            uploadButton.setText("📄 " + selectedFile.getName());
        }
    }

    @FXML
    private void upload() {
        if (selectedFile == null || titleField.getText().isEmpty() || subjectCombo.getValue() == null) {
            showAlert("Заполните все поля и выберите файл");
            return;
        }

        Connection conn = null;
        try {
            conn = org.example.yptapp.util.DBConnection.getConnection();
            conn.setAutoCommit(false); // Транзакция

            Files.createDirectories(BASE_DIR);
            String storedName = System.currentTimeMillis() + "_" + selectedFile.getName();
            Path target = BASE_DIR.resolve(storedName);
            Files.copy(selectedFile.toPath(), target, StandardCopyOption.REPLACE_EXISTING);

            Material m = new Material();
            m.setSubjectId(subjectCombo.getValue().getId());
            m.setTitle(titleField.getText());
            m.setFilePath(target.toString()); // Абсолютный путь пока оставим, но уникальное имя
            m.setFileName(selectedFile.getName());
            m.setFileType(selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1));

            dao.add(m); // нужно передать conn для транзакции, но текущий DAO не поддерживает это
            // Для полной транзакции нужно перегрузить dao.add(Connection, Material)

            conn.commit();

            // Уведомление всем студентам (упрощённо)
            Notification n = new Notification();
            n.setUserId(UserSession.getInstance().getUserId());
            n.setTitle("Новый материал");
            n.setMessage("Загружен: " + m.getTitle());
            notifDao.add(n);

            load();
            titleField.clear();
            uploadButton.setText("📁 Выбрать файл");
            selectedFile = null;
        } catch (IOException | SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            showAlert("Ошибка загрузки: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @FXML
    private void delete() {
        Material sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            dao.delete(sel.getId());
            load();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openFile() {
        Material sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        try {
            java.awt.Desktop.getDesktop().open(new File(sel.getFilePath()));
        } catch (IOException e) {
            showAlert("Не удалось открыть файл");
        }
    }

    @FXML
    private void back() { SceneManager.switchScene("main.fxml", "Главное меню"); }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}