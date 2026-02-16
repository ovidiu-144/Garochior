package org.Garochior.model;

import java.util.ArrayList;

public class Player {
    private int id; //0-4
    public ArrayList<Card> hand;
    private int score;
    private Card selectedCard;
    public boolean myTurn;

    public final Object lockCardSelect = new Object();
    public final Object lockCardRemove = new Object();

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
        System.out.println("Player " + (id + 1) + " score updated: " + score);
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
        synchronized (lockCardSelect){
            selectedCard = hand.get(index);
            lockCardSelect.notifyAll();
        }
        //selectedCard = hand.get(index);
    }

    public Card selectCard (){
        selectedCard = null;
        /// pentru teste, selectam o carte random din mana
//        Random rand = new Random();
//        int size = hand.size();
//        int n = rand.nextInt(size);
//        System.out.println(n);
//        System.out.println("Player " + id + " selected card: " + hand.get(n));
//        return hand.get(n);
        synchronized (lockCardSelect){
            while (selectedCard == null){
                try {
                    lockCardSelect.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Player " + (id + 1) + " selected card: " + selectedCard);
        return selectedCard;
    }
    public void removeCardFromHand (Card card){
        //scoate cartea din mana dupa ce a fost validata de server
        hand.remove(card);
    }

    public int getId() {
        return id;
    }
}
