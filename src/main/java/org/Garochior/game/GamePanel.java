package org.Garochior.game;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Player;

public class GamePanel {
        public GamePanelController start (Stage stage, Player player) throws Exception {
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

                /// pentru test sa vedem ca nu crapa
                /// Assets.init();

                stage.show();
                return gameCtrl;
    }
}
