package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.Comparator;
import java.util.List;

public class KingGame extends ValidationLogic{
    Card card = new Card(ModelConfig.K, CardType.HEARTS);

    public KingGame(int playerTurn) {
        super(playerTurn);
    }

    @Override
    public String getName() {
        return "King of Hearts Game";
    }

    @Override
    public void updateScore(Player player) {
        if (selectedCards.contains(card)){
            System.out.println("Player " + (player.getId() + 1) + " selected the King of Hearts!");
            player.updateScore(-8);
            isOver = true;
        }
        clearSelectedCard();

        System.out.println(selectedCards);
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
            ///Nu avem carte sa punem
            Card firstCard = selectedCards.getFirst();
            if (!hasCard(player.hand)) {
                if (hasKing(player.hand)) {
                    selectedCard = new Card (ModelConfig.K, CardType.HEARTS);
                }
                    else if (hasHeartAce(player.hand)) {
                        selectedCard = new Card (ModelConfig.A, CardType.HEARTS);
                    }
                    else {
                        selectedCard = player.hand.stream()
                                .max(Comparator.comparingInt(Card::getNumber))
                                .orElse(player.hand.getFirst());
                    }

                }
            else {
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
                    if (heartKingIsPlayed()){
                        selectedCard = player.hand.stream()
                                .filter(c -> c.getType() == firstCard.getType())
                                .min(Comparator.comparingInt(Card::getNumber))
                                .orElse(player.hand.getFirst());
                    }
                    else {
                        if (smallestCard.getNumber() > biggestPlayedCard && selectedCards.size() == 3) {
                            if (firstCard.getType() == CardType.HEARTS) {
                                selectedCard = player.hand.stream()
                                        .filter(c -> c.getType() == firstCard.getType())
                                        .min(Comparator.comparingInt(Card::getNumber))
                                        .orElse(player.hand.getFirst());
                            } else {

                                selectedCard = player.hand.stream()
                                        .filter(c -> c.getType() == firstCard.getType())
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
                }

            }
        }
        player.removeCardFromHand(selectedCard);
        selectedCards.add(selectedCard);
        return selectedCard;
    }

    private boolean hasKing (List<Card> hand){
        for (Card card : hand){
            if (card.getNumber() == ModelConfig.K && card.getType() == CardType.HEARTS)
                return true;
        }
        return false;
    }

    private boolean hasHeartAce (List<Card> hand){
        for (Card card : hand){
            if (card.getType() == CardType.HEARTS &&  card.getNumber() == ModelConfig.A)
                return true;
        }
        return false;
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

    private boolean heartKingIsPlayed (){
        for (Card card : selectedCards){
            if (card.getType() == CardType.HEARTS &&  card.getNumber() == ModelConfig.A){
                return true;
            }
        }
        return false;
    }
}
