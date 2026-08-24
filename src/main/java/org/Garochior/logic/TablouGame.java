package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

import java.util.ArrayList;
import java.util.List;

public class TablouGame implements GameLogic {

    public final List<Card> tablouCards = new ArrayList<>();
    private int cardCount = 4;

    private int playerTurn = 0;

    private int playerOver = 4;
    private boolean[] playerGotScore = new boolean[4];

    private java.util.function.Consumer<Integer> onInvalidCard;
    public void setOnInvalidCard(java.util.function.Consumer<Integer> callback) {
        this.onInvalidCard = callback;
    }

    public TablouGame() {
        //adaugam initial toate J-urile in lista de carti pentru jocul Tablou
        for (int i = 0; i < 4; ++i){
            tablouCards.add(new Card(ModelConfig.J, CardType.values()[i]));
        }
    }


    @Override
    public String getName() {
        return "Tablou Game";
    }

    @Override
    public void updateScore(Player player) {

        if (player.hand.isEmpty() && !playerGotScore[player.getId()]) {
            playerGotScore[player.getId()] = true;
            int score;
            if (playerOver == 1)
                score = 0;
            else
                score = (int) Math.pow(2, playerOver);
            playerOver--;
            player.updateScore(score);
        }
    }

    @Override
    public void validateMove(Player player) {
        //avem lista de carti selectate, verificam daca sunt valide pentru jocul respectiv



        if (!hasCard(player)) {
            //Nu are carte, dam skip la tura
            //ceva sa pun ca dau skip maybe
            System.out.println("Player " + player.getId() + "nu are carte sa puna");
            System.out.println("Mana lui: " + player.hand);
        }
        else {
            if (!tablouCards.isEmpty()) {

                while (true) {
                    Card card = player.selectCard(null);
                    if (tablouCards.contains(card)) {
                        player.removeCardFromHand(card);
                        addCardToList(card);
                        //randul urmator
                        updateScore(player);
                        break;
                    }
                    if (onInvalidCard != null) {
                        onInvalidCard.accept(player.getId());
                    }
                    System.out.println("Player " + (player.getId() + 1) + " selected invalid card: " + card);

                }
            }
        }
    }
    //inseamna ca e bun
    private void addCardToList (Card card){
        if (card.getNumber() == ModelConfig.J){
            Card leftCard = new Card(10, card.getType());
            Card rightCard = new Card(ModelConfig.Q, card.getType());

            tablouCards.add(leftCard);
            tablouCards.add(rightCard);
        }
        else if (card.getNumber() > 7 &&  card.getNumber() <= ModelConfig.J ){
            Card leftCard = new Card (card.getNumber() - 1, card.getType());
            tablouCards.add(leftCard);
        }
        else if (card.getNumber() < ModelConfig.A &&  card.getNumber() > ModelConfig.J ){
            Card rightCard = new Card (card.getNumber() + 1, card.getType());
            tablouCards.add(rightCard);
        }

        //scoatem cartea buna
        tablouCards.remove(card);

    }

    private boolean hasCard (Player player){
        for (Card card : player.hand) {
            if (tablouCards.contains(card)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int nextPlayer() {
        return (playerTurn + 1) % 4;
    }

    @Override
    public boolean isOver() {
        return playerOver <= 0;
    }
}
