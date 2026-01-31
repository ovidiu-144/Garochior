package org.Garochior.model;

import java.util.List;

public class Player {
    public int id; //0-4
    public List<Card> hand;
    public int score;
    public Card selectedCard;

    public Player (int id){
        this.id = id;
        hand = List.of();
        score = 0;
    }

    public void setHand (List<Card> hand){
        this.hand = hand;
    }

    @Override
    public String toString() {
        return "Player{" + id +
                ", hand=" + hand +
                '}';
    }

    public void selectCard (){
        //selecteaza o carte din interfata
        //cartea respectiva va fi salvata in selectedCard
        //si apoi scoasa din mana
    }
    public void removeCardFromHand (Card card){
        //scoate cartea din mana dupa ce a fost validata de server
        hand.remove(card);
    }
}
