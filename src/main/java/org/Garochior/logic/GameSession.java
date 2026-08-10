package org.Garochior.logic;

import javafx.application.Platform;
import org.Garochior.game.GamePanelController;
import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.List;

public class GameSession {
    private List<Player> players;
    private final Deck deck;
    private GameLogic game;
    private int firstPlayer;
    private int currentRound;
    private List<GamePanelController> uiControllers;

    public GameSession (List<Player> players, GameLogic game){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        this.uiControllers = null;
        deck.shuffle();
        firstPlayer = 0;
        currentRound = 0;
    }

    public GameSession (List<Player> players, GameLogic game, List<GamePanelController> uiControllers){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        this.uiControllers = uiControllers;
        deck.shuffle();
        firstPlayer = 0;
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
                    int currentPlayer = (firstPlayer + i) % 4;
                    players.get(currentPlayer).myTurn.set(true);
                    game.validateMove(players.get(currentPlayer));
                    players.get(currentPlayer).myTurn.set(false);
                }
                firstPlayer = game.nextPlayer();
                game.updateScore(players.get(firstPlayer));
                
                System.out.println(">>> Player " + (firstPlayer + 1) + " took the hand! <<<");
                
                // Afișează pe interfață pe toți jucătorii
                if (uiControllers != null) {
                    for (int i = 0; i < 4; ++i) {
                        uiControllers.get(i).showHandTaker(firstPlayer);
                    }
                }
                
                try {
                    Thread.sleep(3000); // Pauza de 3 secunde
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

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
