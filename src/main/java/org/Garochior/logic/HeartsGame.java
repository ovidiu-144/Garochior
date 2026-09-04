package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.CardType;
import org.Garochior.model.Player;

public class HeartsGame extends ValidationLogic{
    int totalHearts = 0;

    public HeartsGame(int playerTurn) {
        super(playerTurn);
    }

    @Override
    public String getName() {
        return "Hearts Game";
    }

    @Override
    public void updateScore(Player player) {
        //Numaram cate inimi a luat player-ul
        CardType cardType = CardType.HEARTS;
        int numberOfHearts = 0;
        for (Card card : selectedCards){
            if (card.getType() == cardType){
                numberOfHearts++;
            }
        }
        totalHearts += numberOfHearts;
        if (totalHearts == 8)
            isOver = true;
        player.updateScore(-numberOfHearts);
        clearSelectedCard();
    }
}
