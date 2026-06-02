module org.example.yptapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    opens org.example.yptapp to javafx.fxml;
    opens org.example.yptapp.controller to javafx.fxml;
    opens org.example.yptapp.model to javafx.base;
    exports org.example.yptapp;
}