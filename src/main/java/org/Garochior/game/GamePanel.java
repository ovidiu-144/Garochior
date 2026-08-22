package org.Garochior.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
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
                Scene scene = new Scene(root);

                // 4. Configurează stage-ul
                stage.setTitle("Player " + (player.getId() + 1 ));
                stage.setScene(scene);

                stage.setMaximized(true);

                stage.show();
                return gameCtrl;
    }
    public void returnToMainMenu() throws Exception {
        MainMenu mainMenu = new MainMenu();
        mainMenu.start(stage);
    }
}
