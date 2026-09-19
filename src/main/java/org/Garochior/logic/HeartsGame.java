package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.Comparator;
import java.util.List;

public class HeartsGame extends ValidationLogic{
    int totalHearts = 0;

    public HeartsGame(int playerTurn) {
        super(playerTurn);
    }

    @Override
    public String getName() {
        return "Hearts Game";
    }

    @Override
    public void updateScore(Player player) {
        //Numaram cate inimi a luat player-ul
        CardType cardType = CardType.HEARTS;
        int numberOfHearts = 0;
        for (Card card : selectedCards){
            if (card.getType() == cardType){
                numberOfHearts++;
            }
        }
        totalHearts += numberOfHearts;
        if (totalHearts == 8)
            isOver = true;
        player.updateScore(-numberOfHearts);
        clearSelectedCard();
    }

    @Override
    public Card selectAICard(Player player) {
        Card selectedCard;

        //daca incepe el tura
        if (selectedCards.isEmpty()) {
            firstPlayer = player.getId();
            selectedCard = player.hand.stream()
                    .min(Comparator.comparingInt(Card::getNumber))
                    .orElse(player.hand.getFirst());
        }
        else {
            Card firstCard = selectedCards.getFirst();

            //daca n are carte
            if (!hasCard(player.hand)) {

                if (hasHearts(player.hand)) {
                    selectedCard = player.hand.stream()
                            .filter(c -> c.getType() ==  CardType.HEARTS)
                            .max(Comparator.comparingInt(Card::getNumber))
                            .orElse(player.hand.getFirst());
                }
                else {
                    selectedCard = player.hand.stream()
                            .max(Comparator.comparingInt(Card::getNumber))
                            .orElse(player.hand.getFirst());
                }

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

    private boolean hasHearts (List<Card> hand){
        for (Card card : hand){
            if (card.getType() == CardType.HEARTS)
                return true;
        }
        return false;
    }

}
