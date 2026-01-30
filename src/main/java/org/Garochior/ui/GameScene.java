package org.Garochior.ui;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GameScene {

    public void start (Stage stage) throws Exception {
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();

        Image img = new Image(getClass().getResourceAsStream("/background/barca_25-26.jpg"));
        ImageView background = new ImageView(img);

        background.setFitHeight(screenHeight);
        background.setFitWidth(screenWidth);

        StackPane root = new StackPane();
        root.getChildren().add(background);

        Scene scene = new Scene(root, screenWidth, screenHeight);

        stage.setTitle("Ye");
        stage.setScene(scene);
        stage.show();
    }
}
