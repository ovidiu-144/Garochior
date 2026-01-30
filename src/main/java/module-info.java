module org.Garochior.Garochior {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens org.Garochior to javafx.graphics, javafx.fxml;
    exports org.Garochior;
}