package org.Garochior.logic;

import org.Garochior.constants.ModelConfig;
import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

public class KingGame extends ValidationLogic{
    Card card = new Card(ModelConfig.K, CardType.HEARTS);

    @Override
    public String getName() {
        return "King of Hearts Game";
    }

    @Override
    public void updateScore(Player player) {
        if (selectedCards.contains(card)){
            System.out.println("Player " + (player.getId() + 1) + " selected the King of Hearts!");
            player.updateScore(-8);
            isOver = true;
        }
        clearSelectedCard();

        System.out.println(selectedCards);
    }
}
