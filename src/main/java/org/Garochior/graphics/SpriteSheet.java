package org.Garochior.graphics;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import org.Garochior.constants.ModelConfig;

import java.util.Objects;

public class SpriteSheet {
    private final Image spriteSheet;
    private final int width;
    private final int height;

    public SpriteSheet(String path){
        spriteSheet = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        width = (int) spriteSheet.getWidth();
        height = (int) spriteSheet.getHeight();
        System.out.println("Size: " + spriteSheet.getWidth() + "x" + spriteSheet.getHeight());
    }

    public Image crop (int x, int y, int nrX, int nrY){
        PixelReader reader = spriteSheet.getPixelReader();
        int sizeX = width / nrX;
        int sizeY = height / nrY;
        return new WritableImage(reader, x * sizeX, y * sizeY, sizeX, sizeY);
    }
}
