package org.Garochior.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.List;

public class GamePanelController {

    public VBox centerVBox;
    public HBox centerHBox;
    public Label turnLabel;
    public Label gameLabel;
    private Player player;


    public ImageView background;
    public HBox player1Box;

    public void setPlayer (Player player){
        this.player = player;
    }

    @FXML
    public void initialize() {
        System.out.println("GamePanelController initialized");

        setBackCards(player1Box);
        setBackCards(centerVBox);
        setBackCards(centerHBox);
    }

    private ImageView createImage (){
        ImageView iv = new ImageView();
        iv.setFitWidth(100);
        iv.setFitHeight(150);
        iv.setOnMouseClicked(this::handleCardClicked);
        return iv;
    }

    private void setBackCards (Pane playerBox){
        for (int i = 0; i < playerBox.getChildren().size(); i++) {
            if (playerBox.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) playerBox.getChildren().get(i);
                if (i < 8 && iv.getImage() == null) {
                    iv.setImage(Assets.backCardImages[0]);
                }
            }
        }
        //ImageView iv = centerVBox.getChildren().get(0) instanceof ImageView ? (ImageView) centerVBox.getChildren().get(0) : null;
    }
    public void setHand (){
        player1Box.getChildren().clear();

        //playerBox1 este playerul principal pentru fiecare
        for (int i = 0; i < player.hand.size(); i++) {

            ImageView iv = createImage();
            player1Box.getChildren().add(iv);

            //i reprezinta pozitia cartii in hand
            CardType type = player.hand.get(i).getType();
            int number = player.hand.get(i).getNumber();
            int typeIndex = type.ordinal(); // 0-3 pentru cele 4 tipuri
            int numberIndex = number - 7; // 0-7 pentru cărțile de la 7 la A (7=0, 8=1, ..., A=
            iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
        }
    }

    public void setPlayedCards (Card card, int index){
        //daca index ul e acelasi cu al meu pun cartea in fata, restu punem in funcite de index u propriu
        int id = player.getId();
        CardType type = card.getType();
        int number = card.getNumber();
        int typeIndex = type.ordinal(); // 0-3 pentru cele 4 tipuri
        int numberIndex = number - 7; // 0-7 pentru cărțile de la 7 la A (7=0, 8=1, ..., A=

        if (index == id){
            ImageView iv = (ImageView) centerVBox.getChildren().get(2);
            iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
        }
        else if (index == (id + 1) % 4){
            ImageView iv = (ImageView) centerHBox.getChildren().get(1);
            iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
        }
        else if (index == (id + 2) % 4){
            ImageView iv = (ImageView) centerVBox.getChildren().get(0);
            iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
        }
        else if (index == (id + 3) % 4){
            ImageView iv = (ImageView) centerHBox.getChildren().get(0);
            iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
        }
    }

    public void clearPlayedCards (){
        for (int i = 0; i < centerVBox.getChildren().size(); i++) {
            if (centerVBox.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) centerVBox.getChildren().get(i);
                iv.setImage(Assets.backCardImages[0]);
            }
        }
        for (int i = 0; i < centerHBox.getChildren().size(); i++) {
            if (centerHBox.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) centerHBox.getChildren().get(i);
                iv.setImage(Assets.backCardImages[0]);
            }
        }
    }

    public void handleCardClicked(MouseEvent mouseEvent) {
        if (!player.myTurn.getValue()) {
            System.out.println("It's not your turn!");
            return;
        }
        System.out.println("Card clicked from player: " + player.getId());

        ImageView clickedCard = (ImageView) mouseEvent.getSource();
        int index = player1Box.getChildren().indexOf(clickedCard);
        System.out.println("Card clicked at index: " + index);
        System.out.println("Card is: " + player.hand.get(index));
        player.setSelectedCard(index);
    }

    public void setTurnLabel (int id){
        turnLabel.setText("Player " + (id + 1) + "'s turn");
    }
    public void setGameLabel (String name){
        gameLabel.setText(name);
    }

    public void showHandTaker(int playerWinnerId) {
        Platform.runLater(() -> {
            turnLabel.setText(">>> Player " + (playerWinnerId + 1) + " took the hand! <<<");
        });
    }
}

//creeaza scor si nume pe interfata + creeare Tablou

