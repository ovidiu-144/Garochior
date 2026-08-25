package org.Garochior.game;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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

public class GamePanelController {

    public VBox centerVBox;
    public HBox centerHBox;
    public Label turnLabel;
    public Label gameLabel;
    public VBox pauseMenu;

//    ///-------Buttons for cards
//
//    /// A
//    public Button btnA_DIAMONDS;
//    public Button btnA_CLUBS;
//    public Button btnA_HEARTS;
//    public Button btnA_SPADES;
//
//    /// K
//    public Button btnK_DIAMONDS;
//    public Button btnK_CLUBS;
//    public Button btnK_HEARTS;
//    public Button btnK_SPADES;
//
//    /// Q
//    public Button btnQ_DIAMONDS;
//    public Button btnQ_CLUBS;
//    public Button btnQ_HEARTS;
//    public Button btnQ_SPADES;
//
//    /// J
//    public Button btnJ_DIAMONDS;
//    public Button btnJ_CLUBS;
//    public Button btnJ_HEARTS;
//    public Button btnJ_SPADES;
//
//    /// 10
//    public Button btn10_DIAMONDS;
//    public Button btn10_CLUBS;
//    public Button btn10_HEARTS;
//    public Button btn10_SPADES;
//
//    /// 9
//    public Button btn9_DIAMONDS;
//    public Button btn9_CLUBS;
//    public Button btn9_HEARTS;
//    public Button btn9_SPADES;
//
//    /// 8
//    public Button btn8_DIAMONDS;
//    public Button btn8_CLUBS;
//    public Button btn8_HEARTS;
//    public Button btn8_SPADES;
//
//    /// 7
//    public Button btn7_DIAMONDS;
//    public Button btn7_CLUBS;
//    public Button btn7_HEARTS;
//    public Button btn7_SPADES;
//    public VBox tablouBox;

    @FXML
    public VBox tabouGridBox;


    private Player player;
    public ImageView background;
    public HBox player1Box;

    private Runnable onDisconnect;

    public void setOnDisconnect(Runnable callback) {
        this.onDisconnect = callback;
    }

    public void setPlayer (Player player){
        this.player = player;
    }

    @FXML
    public void initialize() {
        System.out.println("GamePanelController initialized");

        setBackCards(player1Box);
        setBackCards(centerVBox);
        setBackCards(centerHBox);
        initCardGrid();


        Platform.runLater(() -> {
            pauseMenu.getScene().setOnKeyPressed(event -> {
                if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                    if (!pauseMenu.isVisible()) {
                        onEscClicked(new ActionEvent());
                    } else {
                        onResumeClicked(new ActionEvent());
                    }
                }
            });
        });
    }

    private final ImageView[][] cardGridImages = new ImageView[4][8]; // [tip][valoare]

    private void initCardGrid() {
        CardType[] types = CardType.values(); // DIAMONDS, CLUBS, HEARTS, SPADES
        int[] numbers = {7, 8, 9, 10, 11, 12, 13, 14}; // 7 → A

        for (int t = 0; t < 4; t++) {
            HBox row = new HBox(10);
            row.setAlignment(javafx.geometry.Pos.CENTER);

            for (int n = 0; n < 8; n++) {
                int number = numbers[n];
                CardType type = types[t];

                ImageView iv = new ImageView();
                iv.setFitWidth(100);
                iv.setFitHeight(140);
                iv.setPreserveRatio(false);

                int typeIndex = type.ordinal();
                int numberIndex = number - 7;
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
                iv.setStyle("-fx-background-color: transparent; -fx-opacity: 0.4;");

                // Doar J-urile vizibile inițial
                boolean isJ = (number == 11);
                iv.setVisible(isJ);
                iv.setManaged(isJ);

                cardGridImages[t][n] = iv;
                row.getChildren().add(iv);
            }
            tabouGridBox.getChildren().add(row);
        }
    }

    public void setTablouMode(boolean active) {
        System.out.println("Am apasat set TabouMode");
        tabouGridBox.setVisible(active);
        tabouGridBox.setManaged(active);
        tabouGridBox.setMouseTransparent(true);
        centerVBox.setVisible(!active);
        centerVBox.setManaged(!active);
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

    public void setPlayedCards (Card card, int index, boolean isTablou){

        if (!isTablou) {
            //daca index ul e acelasi cu al meu pun cartea in fata, restu punem in funcite de index u propriu
            int id = player.getId();
            CardType type = card.getType();
            int number = card.getNumber();
            int typeIndex = type.ordinal(); // 0-3 pentru cele 4 tipuri
            int numberIndex = number - 7; // 0-7 pentru cărțile de la 7 la A (7=0, 8=1, ..., A=

            if (index == id) {
                ImageView iv = (ImageView) centerVBox.getChildren().get(2);
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
            } else if (index == (id + 1) % 4) {
                ImageView iv = (ImageView) centerHBox.getChildren().get(1);
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
            } else if (index == (id + 2) % 4) {
                ImageView iv = (ImageView) centerVBox.getChildren().get(0);
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
            } else if (index == (id + 3) % 4) {
                ImageView iv = (ImageView) centerHBox.getChildren().get(0);
                iv.setImage(Assets.cardsImages[typeIndex][numberIndex]);
            }
        }
        else {
            setTablouCard (card);
            setCardVisibility (card);
        }
    }

    public void clearPlayedCards () {
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

    public void clearTablouPlayedCards () {
        for (ImageView[] ivType : cardGridImages){
            for  (int i = 7 ; i <= 14; ++i) {
                if (i != 11){
                    ivType[i - 7].setVisible(false);
                    ivType[i - 7].setManaged(false);
                }
                ivType[i - 7].setStyle("-fx-background-color: transparent; -fx-opacity: 0.4;");
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

    public void setTablouCard (Card card){
        int typeIndex = card.getType().ordinal();
        int numberIndex = card.getNumber() - 7;
        ImageView iv = cardGridImages[typeIndex][numberIndex];
        iv.setDisable(true);
        iv.setStyle("-fx-background-color: transparent; -fx-opacity: 1;");
    }

    public void setCardVisibility (Card card) {
//        CardType[] types = CardType.values(); // DIAMONDS, CLUBS, HEARTS, SPADES
//        int[] numbers = {7, 8, 9, 10, 11, 12, 13, 14}; // 7 → A

        int typeIndex = card.getType().ordinal();
        int numberIndex = card.getNumber() - 7;

        //setam visibilitatea si in stanga si in dreapta
        if (card.getNumber() == 11) { //
            ImageView leftIv = cardGridImages[typeIndex][numberIndex - 1];
            ImageView rightIv = cardGridImages[typeIndex][numberIndex + 1];
            leftIv.setVisible(true);
            leftIv.setManaged(true);
            rightIv.setVisible(true);
            rightIv.setManaged(true);
        }
        else if (card.getNumber() < 11 && card.getNumber() >= 8) {  // 7 < number < J
            ImageView leftIv = cardGridImages[typeIndex][numberIndex - 1];
            leftIv.setVisible(true);
            leftIv.setManaged(true);
        }
        else  if (card.getNumber() > 8 && card.getNumber() <= 13) {
            ImageView rightIv = cardGridImages[typeIndex][numberIndex + 1];
            rightIv.setVisible(true);
            rightIv.setManaged(true);
        }
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

    @FXML
    public void onEscClicked(ActionEvent actionEvent) {
        pauseMenu.setVisible(true);
    }

    @FXML
    public void onResumeClicked(ActionEvent actionEvent) {
        pauseMenu.setVisible(false);
    }

    @FXML
    public void onDisconnectClicked(ActionEvent actionEvent) {
        pauseMenu.setVisible(false);
        if (onDisconnect != null) {
            onDisconnect.run();
        }
    }
}

//creeaza scor si nume pe interfata + creeare Tablou

