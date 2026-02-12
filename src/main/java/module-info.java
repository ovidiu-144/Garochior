module org.Garochior.Garochior {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens org.Garochior to javafx.graphics, javafx.fxml;
    opens org.Garochior.ui to javafx.graphics, javafx.fxml;
    opens org.Garochior.game to javafx.graphics, javafx.fxml;

    exports org.Garochior;
    exports org.Garochior.ui;
    exports org.Garochior.game;
}