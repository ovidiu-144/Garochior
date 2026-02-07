package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.Player;

import java.util.List;

public abstract class ValidationLogic implements GameLogic{
    public List<Card> selectedCards;
    public boolean isOver = false;
    //ceva sa selectam cartile, pe care le adaugam in lista, ca sa stim ordinea lor, prima carte mereu fiind a jucatorului care a inceput tura
    @Override
    public void validateMove(Player player) {
        //avem lista de carti selectate, verificam daca sunt valide pentru jocul respectiv
        if (selectedCards.isEmpty()){
            //selectam cartea
            Card card = player.selectCard();

            //prima carte poate fi orice, in acest pas
            selectedCards.add(card);
        }
        else {
            Card firstCard = selectedCards.getFirst();
            boolean hasCard = player.hasCard(firstCard);

            if (!hasCard){
                Card card = player.selectCard();
                selectedCards.add(card);
            }
            //bucla pana cand selecteaza o carte valida
            while (true) {
                Card card = player.selectCard();
                if (card.getType() == firstCard.getType()){
                    selectedCards.add(card);
                    break;
                }
            }
        }
    }

    @Override
    public int nextPlayer (){
        Card firstCard = selectedCards.getFirst();
        int maxCard = firstCard.getNumber();
        int player = 0;
        for (int poz = 1; poz <= 3; ++poz){
            Card card = selectedCards.get(poz);
            if (card.getNumber() > maxCard && card.getType() == firstCard.getType()){
                maxCard = card.getNumber();
                player = poz;
            }
        }
        return player;
    }
    @Override
    public boolean isOver(){
        return isOver;
    }

    //functie care sa goleasca lista de carti selectate dupa fiecare tura
    public void clearSelectedCard(){
        selectedCards.clear();
    }
}
