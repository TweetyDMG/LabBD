module org.example.labbd {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires org.apache.poi.ooxml;

    opens org.example.labbd to javafx.fxml;
    exports org.example.labbd;
}