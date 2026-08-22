package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class ValidationLogic implements GameLogic{

    //callback pentru cartea selectata
    private java.util.function.Consumer<Integer> onInvalidCard;

    public List<Card> selectedCards = new ArrayList<>();
    public boolean isOver = false;
    private int firstPlayer = 0;
    //ceva sa selectam cartile, pe care le adaugam in lista, ca sa stim ordinea lor, prima carte mereu fiind a jucatorului care a inceput tura

    public void setOnInvalidCard(java.util.function.Consumer<Integer> callback) {
        this.onInvalidCard = callback;
    }

    @Override
    public void validateMove(Player player) {
        //avem lista de carti selectate, verificam daca sunt valide pentru jocul respectiv
        if (selectedCards.isEmpty()){
            System.out.println("Player " + player.getId() + " starts the round, select any card");

            //selectam cartea
            Card card = player.selectCard(null);

            //prima carte poate fi orice, in acest pas
            selectedCards.add(card);

            //stergem cartea din mana jucatorului
            player.removeCardFromHand(card);
            firstPlayer = player.getId();
        }
        else {
            Card firstCard = selectedCards.getFirst();

            boolean hasCard = player.hasCard(firstCard);

            if (!hasCard){

                Card card = player.selectCard(firstCard);
                selectedCards.add(card);
                player.removeCardFromHand(card);
            }
            else{
                //Start thread pentru a astepta selectarea unei carti valide

                //bucla pana cand selecteaza o carte valida
                while (true) {
                    Card card = player.selectCard(firstCard);
                    if (card.getType() == firstCard.getType()){
                        selectedCards.add(card);
                        player.removeCardFromHand(card);
                        break;
                    }
                    if (onInvalidCard != null) {
                        onInvalidCard.accept(player.getId());
                    }
                    System.out.println("Player " + (player.getId() + 1) + " selected invalid card: " + card + ", must follow suit: " + firstCard.getType());
                }
            }
        }
    }

    @Override
    public int nextPlayer (){
        System.out.println(selectedCards);

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
        return (player + firstPlayer) % 4;
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
