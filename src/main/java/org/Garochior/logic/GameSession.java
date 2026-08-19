package org.Garochior.logic;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import org.Garochior.game.GamePanelController;
import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.List;
import java.util.SimpleTimeZone;

public class GameSession {
    private List<Player> players;
    private final Deck deck;
    private GameLogic game;
    public IntegerProperty firstPlayer;
    private int currentRound;
    private List<GamePanelController> uiControllers;



    public GameSession (List<Player> players, GameLogic game, List<GamePanelController> uiControllers){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        this.uiControllers = uiControllers;
        deck.shuffle();
        firstPlayer = new SimpleIntegerProperty(0);

        currentRound = 0;
    }

    public void setHands(){
        //impartim cartile
        for (int i = 0; i < 4; ++i){
            players.get(i).setHand(deck.getPlayerCards(i));
            System.out.println("Player " + (i + 1 )+ ": " + players.get(i));
        }
    }
    public void startGame(Runnable onFinish){
        new Thread(() -> {
        //aici alt thread
            while (currentRound < 8){
                System.out.println("Round " + (currentRound + 1));
                for (int i = 0; i < 4; ++i){
                    int currentPlayer = (firstPlayer.get() + i) % 4;
                    players.get(currentPlayer).myTurn.set(true);
                    game.validateMove(players.get(currentPlayer));
                    players.get(currentPlayer).myTurn.set(false);
                }
                firstPlayer.set(game.nextPlayer());

                game.updateScore(players.get(firstPlayer.get()));

                System.out.println(">>> Player " + (firstPlayer.get() + 1) + " took the hand! <<<");

//                try {
//                    Thread.sleep(3000); // Pauza de 3 secunde
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                }

                if (game.isOver()){

                    break;
                }
                currentRound++;
            }
            /// afisam scorurile
            for (int i = 0; i < 4; ++i){
                System.out.println("Player " + (i + 1) + " score: " + players.get(i).getScore());
            }
            if (onFinish != null){
                Platform.runLater(onFinish);
            }


        }).start();
    }

}
