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
                ///Avem carte sa punem
                int biggestPlayedCard = biggestCard();

                Card smallestCard = player.hand.stream()
                        .filter(c -> c.getType() == firstCard.getType())
                        .min(Comparator.comparingInt(Card::getNumber))
                        .orElse(player.hand.getFirst());  ///Cea mai mica carte din mana

                if (biggestPlayedCard > smallestCard.getNumber()) {
                    selectedCard = player.hand.stream()
                            .filter(c -> c.getType() == firstCard.getType() && c.getNumber() < biggestPlayedCard)
                            .max(Comparator.comparingInt(Card::getNumber))
                            .orElse(player.hand.getFirst());
                }
                else {

                    if (smallestCard.getNumber() > biggestPlayedCard && selectedCards.size() == 3) {
                        selectedCard = player.hand.stream()
                                .filter(c -> c.getType() == firstCard.getType())
                                .max(Comparator.comparingInt(Card::getNumber))
                                .orElse(player.hand.getFirst());
                    }
                    else {
                        selectedCard = player.hand.stream()
                                .filter(c -> c.getType() == firstCard.getType())
                                .min(Comparator.comparingInt(Card::getNumber))
                                .orElse(player.hand.getFirst());
                    }
                }

            }
        }
        player.removeCardFromHand(selectedCard);
        selectedCards.add(selectedCard);
        return selectedCard;
    }

    private int biggestCard (){
        int biggest = 0;
        Card firstCard = selectedCards.getFirst();
        for (Card card : selectedCards){
            if ( card.getType() == firstCard.getType() && card.getNumber() > biggest)
                biggest = card.getNumber();
        }
        return biggest;
    }
}

