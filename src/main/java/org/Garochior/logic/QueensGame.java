package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

public class QueensGame extends ValidationLogic{
    int totalQueens = 0;
    @Override
    public String getName() {
        return "Queens Game";
    }

    @Override
    public void updateScore(Player player) {
        //int queen = ModelConfig.Q;
        for (Card card : selectedCards){
            if (card.getNumber() == ModelConfig.Q){
                totalQueens++;
            }
        }
        if (totalQueens == 4)
            isOver = true;
        player.updateScore(-(totalQueens * 2));
        clearSelectedCard();
    }
}
