package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

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
}
