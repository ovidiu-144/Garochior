package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class QueensGame extends ValidationLogic{
    int totalQueens = 0;

    public QueensGame(int playerTurn) {
        super(playerTurn);
    }

    @Override
    public String getName() {
        return "Queens Game";
    }

    @Override
    public void updateScore(Player player) {
        //int queen = ModelConfig.Q;
        int numberOfQueens = 0;

        for (Card card : selectedCards){
            if (card.getNumber() == ModelConfig.Q){
                numberOfQueens++;
            }
        }
        totalQueens += numberOfQueens;
        if (totalQueens == 4)
            isOver = true;
        player.updateScore(-(numberOfQueens * 2));
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
                if (hasQueens(player.hand)) {

                    //TODO ar trebui sa returneze dama de tipul care are cele mai putine carti
                    //deocamdata o returneaza pe prima gasita
                    selectedCard = player.hand.stream()
                            .filter(c -> c.getNumber() == ModelConfig.Q)
                            .max(Comparator.comparingInt(Card::getNumber))
                            .orElse(player.hand.getFirst());
                }
                else {
                    selectedCard = player.hand.stream()
                            .max(Comparator.comparingInt(Card::getNumber))
                            .orElse(player.hand.getFirst());
                }

            } else {
                /// Daca cea mai mica carte cu care poate sa ia este Dama, sa incerce sa nu o puna.

                Optional<Card> smallestHandCard =
                        player.hand.stream().filter(c -> c.getType() == firstCard.getType())
                        .min(Comparator.comparingInt(Card::getNumber));
                int biggestPlayedCard = biggestCard();


                ///DACA cea mai mare carte jucata este K sau A, ar trebui sa incerc sa pun dama
                if (biggestPlayedCard > ModelConfig.Q && ///daca cartea este K sau A
                        hasQueenType(player.hand, firstCard.getType()) /// am Dama daca care trebuie
                ) {
                    selectedCard = player.hand.stream()
                            .filter(c -> c.getType() == firstCard.getType() &&  c.getNumber() == ModelConfig.Q)
                            .findFirst().get();
                }

                else {
                    ///Daca cea mai mica carte este dama, sa caute una mai mare sa puna, verificam cea mai mare carte din cele puse
                    if (smallestHandCard.isPresent() && smallestHandCard.get().getNumber() == ModelConfig.Q && /// cea mai mica carte din mana este dama
                            biggestPlayedCard < ModelConfig.Q /// cea mai mare carte de jos este mai mica decat Dama
                    ) {
                        selectedCard = player.hand.stream()
                                .filter(c -> c.getType() == firstCard.getType())
                                .max(Comparator.comparingInt(Card::getNumber)) ///INCERC sa pun o carte mai mare ca Dama
                                .orElse(player.hand.getFirst());
                    }

                    /// Daca cea mai mica carte a noastra NU este DAMA
                    /// Verificam cea mai mare carte pusa, si cea mai mica carte a noastra
                    else {

                        ///Cea mai mica carte a noastra este mai mare decat cea mai mare jucata jos -> Cautam o carte mai mare
                        if (smallestHandCard.isPresent() && smallestHandCard.get().getNumber() > biggestPlayedCard && selectedCards.size() == 3) { ///punem asta doar daca stim siguri
                            selectedCard = player.hand.stream()
                                    .filter(c -> c.getType() == firstCard.getType() && c.getNumber() != ModelConfig.Q) ///Cautam o carte mai mare, care nu ar fi DAMA
                                    .max(Comparator.comparingInt(Card::getNumber))
                                    .orElse(player.hand.getFirst());
                        }

                        ///Altfel punem cea mai mica carte
                        else {
                            if (smallestHandCard.isPresent() &&
                                smallestHandCard.get().getNumber() < biggestPlayedCard ){
                                selectedCard = player.hand.stream()
                                        .filter(c -> c.getType() == firstCard.getType() && c.getNumber() < biggestPlayedCard)
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
            }
        }
        player.removeCardFromHand(selectedCard);
        selectedCards.add(selectedCard);
        return selectedCard;
    }

    private boolean hasQueens (List<Card> hand){
        for (Card card : hand){
            if (card.getNumber() == ModelConfig.Q)
                return true;
        }
        return false;
    }

    private boolean hasQueenType (List<Card> hand, CardType cardType){
        for (Card card : hand){
            if (card.getNumber() == ModelConfig.Q && card.getType() == cardType)
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

    private boolean selectedQueens (){
        for (Card card : selectedCards){
            if (card.getNumber() == ModelConfig.Q)
                return true;
        }
        return false;
    }
}
