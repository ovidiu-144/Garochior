package org.Garochior.game;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.Garochior.graphics.Assets;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.ArrayList;
import java.util.List;

public class GamePanelController {

    public VBox centerVBox;
    public HBox centerHBox;
    //punem Player-ul aici
    private Player player;


    public ImageView background;
    public HBox player1Box;
    public VBox player2Box;
    public HBox player3Box;
    public VBox player4Box;

    public List<ImageView> player1Cards;

    public void setPlayer (Player player){
        this.player = player;
    }

    @FXML
    public void initialize() {
        System.out.println("GamePanelController initialized");

        player1Cards = new ArrayList<>();
        for (int i = 0; i < 8; ++i){
            ImageView iv = new ImageView();
            iv.setFitWidth(100);
            iv.setFitHeight(150);
            iv.setOnMouseClicked(this::handleCardClicked);
            player1Cards.add(iv);
            player1Box.getChildren().add(iv);
        }


        setBackCards(player1Box);
//        setBackCards(player2Box);
//        setBackCards(player3Box);
//        setBackCards(player4Box);
        setBackCards(centerVBox);
        setBackCards(centerHBox);
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
    private void setCarti (Pane playerBox, int poz){
        for (int i = 0; i < playerBox.getChildren().size(); i++) {
            if (playerBox.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) playerBox.getChildren().get(i);

                // În mod normal aici vei folosi logica jocului (ce cărți are jucătorul în mână)
                if (i < 8) {
                    iv.setImage(Assets.cardsImages[poz][i]);
                }
            }
        }
    }
    public void setHand (){
        //playerBox1 este playerul principal pentru fiecare
        for (int i = 0; i < player1Box.getChildren().size(); i++) {
            if (player1Box.getChildren().get(i) instanceof ImageView) {
                ImageView iv = (ImageView) player1Box.getChildren().get(i);
                //i reprezinta pozitia cartii in hand
                CardType type = player.hand.get(i).getType();
                int number = player.hand.get(i).getNumber();
                int typeIndex = type.ordinal(); // 0-3 pentru cele 4 tipuri
                int numberIndex = number - 7; // 0-7 pentru cărțile de la 7 la A (7=0, 8=1, ..., A=
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
            }
        }
    }

    public void handleCardClicked(MouseEvent mouseEvent) {
        if (!player.myTurn) {
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


}
