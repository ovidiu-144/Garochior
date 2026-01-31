package org.Garochior.logic;

import org.Garochior.model.Deck;
import org.Garochior.model.Player;

import java.util.List;

public class GameSession {
    private Player[] players;
    private final Deck deck;
    private GameLogic game;

    public GameSession (Player[] players, GameLogic game){
        this.players = players;
        this.game = game;
        this.deck = new Deck();
        deck.shuffle();
    }
    

}
