package org.Garochior.model;

import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Player {
    private final int id;//0-4
    private String name;
    //public ArrayList<Card> hand;
    public ObservableList<Card> hand;

    private int score;
    public Card selectedCard;
    public BooleanProperty myTurn;

    public final Object lockCardSelect = new Object();

    public boolean AiMode = false;

    public Player (int id){
        this.id = id;
        hand = FXCollections.observableArrayList();
        score = 0;
        myTurn = new javafx.beans.property.SimpleBooleanProperty(false);
    }

    public void setHand (ObservableList<Card> hand){
        this.hand.setAll(hand);
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

    public Card selectCard (Card firstCard){
        synchronized (lockCardSelect){
            selectedCard = null;
            if (AiMode){
                //sa para ca gandeste

                //Ai ul meu frumi
                try {
                    Thread.sleep(1000);
//                    selectedCard = selectAiCard(firstCard);
                    selectedCard = selectRandomCard();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            while (selectedCard == null){
                try {
                    lockCardSelect.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (AiMode){
                    //sa para ca gandeste

                    //Ai ul meu frumi
                    try {
                        //Thread.sleep(1500);
                        selectedCard = selectAiCard(firstCard);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        //System.out.println("Player " + (id + 1) + " selected card: " + selectedCard);
        return selectedCard;
    }

    private Card selectAiCard(Card firstCard) {
        if (firstCard == null) {
            return hand.stream()
                    .min(Comparator.comparingInt(Card::getNumber))
                    .orElse(hand.getFirst());
        }

        if (!hasCard(firstCard.getType())) {
            return hand.stream()
                    .max(Comparator.comparingInt(Card::getNumber))
                    .orElse(hand.getFirst());
        }

        return hand.stream()
                .filter(c -> c.getType() == firstCard.getType())
                .min(Comparator.comparingInt(Card::getNumber))
                .orElse(hand.getFirst());
    }

//    private Card selecteSmallestPossibleCard (Card firstCard){
//        Card card = null;
//
//        for (Card c: hand){
//            if (c.getType() == firstCard.getType()){
//                if (card == null || c.getNumber() < card.getNumber()){
//                    card = c;
//                }
//            }
//        }
//        return card;
//    }

    private boolean hasCard (CardType cardType){
        for (Card c : hand){
            if (c.getType() == cardType)
                return true;
        }
        return false;
    }



    public Card selectRandomCard (){
        //pauza pentru a simula timpul de gandire al AI-ului

        Random rand = new Random();
        int size = hand.size();
        int n = rand.nextInt(size);
        //System.out.println("Player " + (id + 1) + " selected card: " + hand.get(n));
        return hand.get(n);
    }

    public void removeCardFromHand (Card card){
        //scoate cartea din mana dupa ce a fost validata de server
        //hand.remove(card);
        hand.remove(card);
    }

    public int getId() {
        return id;
    }
}
