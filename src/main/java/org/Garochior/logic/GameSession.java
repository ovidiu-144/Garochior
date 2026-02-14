package org.Garochior.logic;

import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.List;

public class GameSession {
    private List<Player> players;
    private final Deck deck;
    private GameLogic game;
    private int firstPlayer;
    private int currentRound;

    public GameSession (List<Player> players, GameLogic game){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        deck.shuffle();
        firstPlayer = 0;
        currentRound = 0;
    }

    public void setHands(){
        //impartim cartile
        for (int i = 0; i < 4; ++i){
            players.get(i).setHand(deck.getPlayerCards(i));
            System.out.println("Player " + i + ": " + players.get(i));
        }
    }
    public void startGame(){
        
        //aici alt thread
        while (currentRound < 8){
            System.out.println("Round " + (currentRound + 1));
            for (int i = 0; i < 4; ++i){
                int currentPlayer = (firstPlayer + i) % 4;
                players.get(currentPlayer).myTurn = true;
                game.validateMove(players.get(currentPlayer));
                players.get(currentPlayer).myTurn = false;
            }
            firstPlayer = game.nextPlayer();
            game.updateScore(players.get(firstPlayer));
            if (game.isOver()){
                break;
            }
            currentRound++;
        }

        /// afisam scorurile
        for (int i = 0; i < 4; ++i){
            System.out.println("Player " + i + " score: " + players.get(i).getScore());
        }
    }
    

}
