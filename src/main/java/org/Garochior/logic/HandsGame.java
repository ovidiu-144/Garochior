package org.Garochior.logic;

import org.Garochior.model.Player;

public class HandsGame extends ValidationLogic{
    public HandsGame(int playerTurn) {
        super(playerTurn);
    }

    @Override
    public String getName() {
        return "Hands Game";
    }

    @Override
    public void updateScore(Player player) {
        //int nextPlayer = nextPlayer();
        //Scadem 1 de fiecare data cand cineva ia o mana, adica in cazul nostru ar fi urmatoarea persoana
        player.updateScore(-1);
        clearSelectedCard();
    }
}
