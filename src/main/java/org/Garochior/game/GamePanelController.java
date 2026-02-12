package org.Garochior.game;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.Garochior.graphics.Assets;

public class GamePanelController {
    public ImageView background;

    public HBox player1Box;

    @FXML
    public void initialize() {
        System.out.println("GamePanelController initialized");
        for (int i = 0; i < player1Box.getChildren().size(); i++) {
            if (player1Box.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) player1Box.getChildren().get(i);

                // În mod normal aici vei folosi logica jocului (ce cărți are jucătorul în mână)
                if (i < 8) {
                    iv.setImage(Assets.backCardImages[2]);
                }
            }
        }
    }

    public void testCarti() {
        for (int i = 0; i < player1Box.getChildren().size(); i++) {
            if (player1Box.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) player1Box.getChildren().get(i);

                // În mod normal aici vei folosi logica jocului (ce cărți are jucătorul în mână)
                if (i < 8) {
                    iv.setImage(Assets.cardsImages[0][i]);
                }
            }
        }
    }
}
