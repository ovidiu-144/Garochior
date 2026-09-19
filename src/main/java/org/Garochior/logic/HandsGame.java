package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.Player;

import java.util.Comparator;
import java.util.List;

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

    @Override
    public Card selectAICard(Player player) {
        Card selectedCard;

        if (selectedCards.isEmpty()) {
            firstPlayer = player.getId();
            selectedCard = player.hand.stream()
                    .min(Comparator.comparingInt(Card::getNumber))
                    .orElse(player.hand.getFirst());
        }
        else {
            Card firstCard = selectedCards.getFirst();
            if (!hasCard(player.hand)) {
                selectedCard = player.hand.stream()
                        .max(Comparator.comparingInt(Card::getNumber))
                        .orElse(player.hand.getFirst());
            } else {
                selectedCard = player.hand.stream()
                        .filter(c -> c.getType() == firstCard.getType())
                        .min(Comparator.comparingInt(Card::getNumber))
                        .orElse(player.hand.getFirst());
            }
        }
        player.removeCardFromHand(selectedCard);
        selectedCards.add(selectedCard);
        return selectedCard;
    }
}
