package org.Garochior.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private int id; //0-4
    public ArrayList<Card> hand;
    private int score;
    public Card selectedCard;
    public boolean myTurn;

    public Player (int id){
        this.id = id;
        hand = new ArrayList<>();
        score = 0;
        myTurn = false;
    }

    public void setHand (ArrayList<Card> hand){
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
        System.out.println("Player " + id + " score updated: " + score);
    }

    //verificam daca avem de pus cartea care trebuie, daca nu putem pune orice
    public boolean hasCard (Card firstCard){
        for (Card card : hand){
            if (card.getType().equals(firstCard.getType()))
                return true;
        }
        return false;
    }
    public void setSelectedCard (int index){
        selectedCard = hand.get(index);
    }

    public Card selectCard (){
        //selecteaza o carte din interfata
        //cartea respectiva va fi salvata in selectedCard
        //si apoi scoasa din mana
        //bucla de selectare a cartii

        //alt thread pentru a astepta selectarea
//        while (selectedCard == null){
//            //asteptam sa fie selectata o carte
//        }
        /// pentru teste, selectam o carte random din mana
        Random rand = new Random();
        int size = hand.size();
        int n = rand.nextInt(size);
        System.out.println(n);


        System.out.println("Player " + id + " selected card: " + hand.get(n));
        return hand.get(n);
    }
    public void removeCardFromHand (Card card){
        //scoate cartea din mana dupa ce a fost validata de server
        hand.remove(card);
    }

    public int getId() {
        return id;
    }
}
