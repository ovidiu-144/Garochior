package org.Garochior.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//contine toate cartile din joc 7-A
//aici se intampla amestecarea lor
//dar nu si impartirea lor
public class Deck {

    private final List<Card> deck;
    public Deck() {
        deck = Arrays.asList(new Card[32]);
        for (CardType type : CardType.values()){
            for (int number = 7; number <= 14; ++number){
                deck.set(type.ordinal() * 8 + number - 7, new Card(number, type));
            }
        }
    }
    public void shuffle(){
        Collections.shuffle(deck);
        System.out.println("Deck shuffled");
        System.out.println(this);
    }

    //Logica de impartire a cartilor
    public List<Card> getDeck (){
        return deck;
    }
    public ObservableList<Card> getPlayerCards(int player){
        //return Arrays.copyOfRange(deck, player * 8, player * 8 + 8)

        return FXCollections.observableArrayList((deck.subList(player * 8, player * 8 + 8)));
    }

    @Override
    public String toString() {
        return "Deck{" +
                "deck=" + deck +
                '}';
    }
}
