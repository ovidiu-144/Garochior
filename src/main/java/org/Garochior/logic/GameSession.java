package org.Garochior.logic;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import org.Garochior.game.GamePanelController;
import org.Garochior.model.Card;
import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.List;
import java.util.SimpleTimeZone;

public class GameSession {
    private List<Player> players;
    private final Deck deck;
    private GameLogic game;
    public int firstPlayer;
    private int currentRound;
    private java.util.function.Consumer<Integer> onHandTaken;
    private boolean isTablou = false;

    public void setOnHandTaken(java.util.function.Consumer<Integer> callback) {
        this.onHandTaken = callback;
    }

    public GameSession (List<Player> players, GameLogic game){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        deck.shuffle();
        firstPlayer = 0;

        currentRound = 0;
        if (game instanceof TablouGame) {
            isTablou = true;
        }
    }

    public void setHands(){
        //impartim cartile
        for (int i = 0; i < 4; ++i){
            players.get(i).setHand(deck.getPlayerCards(i));
            System.out.println("Player " + (i + 1 )+ ": " + players.get(i));
        }
    }
    public void startGame(Runnable onFinish){
        Thread gameThread = new Thread(() -> {
            int maxRounds = isTablou ? 40 : 8;

            while (currentRound < maxRounds){
                System.out.println("Round " + (currentRound + 1));
                for (int i = 0; i < 4; ++i){
                    int currentPlayer = (firstPlayer + i) % 4;
                    players.get(currentPlayer).myTurn.set(true);
                    game.validateMove(players.get(currentPlayer));
                    players.get(currentPlayer).myTurn.set(false);
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if (!isTablou){
                    firstPlayer = game.nextPlayer();
                    game.updateScore(players.get(firstPlayer));
                    System.out.println(">>> Player " + (firstPlayer + 1) + " took the hand! <<<");
                }

                if (!isTablou && onHandTaken != null) {
                    onHandTaken.accept(firstPlayer);
                }



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

        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public String getGameName () {
        return game.getName();
    }
}
