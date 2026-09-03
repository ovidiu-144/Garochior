package org.Garochior.game;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Player;
import org.Garochior.ui.MainMenu;

public class GamePanel {
        private Stage stage;

        public GamePanelController start (Stage stage, Player player) throws Exception {
                this.stage = stage;

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/GamePanel.fxml"));
                AnchorPane root = loader.load();

                GamePanelController gameCtrl = loader.getController();
                //gameCtrl.testCarti();

                gameCtrl.setPlayer(player);
                // 3. Pune rădăcina într-o scenă

                Group scalableGroup = new javafx.scene.Group(root);
                StackPane rootPane = new javafx.scene.layout.StackPane(scalableGroup);
                rootPane.setStyle("-fx-background-color: black;"); // adaugă benzi negre (letterboxing) dacă aspect ratio diferă


                Scene scene = new Scene(rootPane);

//                rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
//                    double scale = newVal.doubleValue() / 1920.0;
//                    scalableGroup.setScaleX(scale);
//                    scalableGroup.setScaleY(scale);
//                });
                Runnable updateScale = () -> {
                    double w = rootPane.getWidth();
                    double h = rootPane.getHeight();

                    // Evităm calculul dacă fereastra nu are încă dimensiuni valide
                    if (w == 0 || h == 0) return;

                    double scaleX = w / 1920.0;
                    double scaleY = h / 1080.0; // Actualizat pentru 1080 conform modificării tale

                    double scale = Math.min(scaleX, scaleY);

                    scalableGroup.setScaleX(scale);
                    scalableGroup.setScaleY(scale);
                };

    // Ascultăm modificările de dimensiune și apelăm logica
                rootPane.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> updateScale.run());

                // 4. Configurează stage-ul
                stage.setTitle("Player " + (player.getId() + 1 ));
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();

                Platform.runLater(() -> {
                    Platform.runLater(updateScale); // double runLater = după ce JavaFX termină layout-ul
                });

                return gameCtrl;
    }
    public void returnToMainMenu() throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start(stage);
    }
}
