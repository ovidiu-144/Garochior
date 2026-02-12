package org.Garochior.graphics;

import javafx.scene.image.Image;

public class Assets {

    //linii avem tipurile, coloane avem valorile
    //ordine este
    /// romb
    /// trefla
    /// inima
    /// caro
    public static Image[][] cardsImages;
    public static Image[] backCardImages;
    public static void init() {
        cardsImages = new Image[4][8];
        SpriteSheet cardSheet = new SpriteSheet ("/Sprites/cards.png");
        /// avem nevoie doar de carti de la 7 la A
        for (int i = 0; i < 4; i++) {
            for (int j = 7; j <= 13; j++) {
                cardsImages[i][j - 7] = cardSheet.crop (j - 1,  i, 13, 4);
                System.out.println("Card " + i + " " + (j - 7) + " loaded");
            }
        }
        /// Asii sunt primii in sprite sheet
        for (int i = 0; i < 4; ++i){
            cardsImages[i][7] = cardSheet.crop (0, i, 13, 4);
            System.out.println("Card " + i + " " + 7 + " loaded");
        }

        SpriteSheet backCardSheet = new SpriteSheet("/Sprites/backCards.png");
        backCardImages = new Image[3];
        for (int i = 0; i < 3; ++i){
            backCardImages[i] = backCardSheet.crop(i, 0, 3, 1);
            System.out.println("Back card " + i + " loaded");
        }
    }
}
