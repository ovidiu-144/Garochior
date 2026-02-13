package org.Garochior.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int id; //0-4
    public List<Card> hand;
    private int score;
    public Card selectedCard;

    public Player (int id){
        this.id = id;
        hand = new ArrayList<>();
        score = 0;
    }

    public void setHand (List<Card> hand){
        this.hand = hand;
    }

    //pentru teste
    @Override
    public String toString() {
        return "Player{" + id +
                ", hand=" + hand +
                '}';
    }
    public int getScore() {
        return score;
    }
    public void updateScore (int points){
        score += points;
    }

    //verificam daca avem de pus cartea care trebuie, daca nu putem pune orice
    public boolean hasCard (Card firstCard){
        for (Card card : hand){
            if (card.getType().equals(firstCard.getType()))
                return true;
        }
        return false;
    }

    public Card selectCard (){
        //selecteaza o carte din interfata
        //cartea respectiva va fi salvata in selectedCard
        //si apoi scoasa din mana
        return null;
    }
    public void removeCardFromHand (Card card){
        //scoate cartea din mana dupa ce a fost validata de server
        hand.remove(card);
    }

    public int getId() {
        return id;
    }
}
