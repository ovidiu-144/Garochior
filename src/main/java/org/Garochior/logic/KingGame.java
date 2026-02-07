package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

public class KingGame extends ValidationLogic{
    @Override
    public String getName() {
        return "King of Hearts Game";
    }

    @Override
    public void updateScore(Player player) {
        Card card = new Card(ModelConfig.K, CardType.HEARTS);
        if (selectedCards.contains(card)){
            player.updateScore(-8);
            isOver = true;
        }
        clearSelectedCard();
    }
}
