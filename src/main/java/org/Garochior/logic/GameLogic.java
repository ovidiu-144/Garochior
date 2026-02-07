package org.Garochior.logic;

import org.Garochior.model.Card;
import org.Garochior.model.Player;

public interface GameLogic {
    String getName();
    void updateScore(Player player);

    //ceva sa selectam cartile, pe care le adaugam in lista, ca sa stim ordinea lor, prima carte mereu fiind a jucatorului care a inceput tura
    void validateMove(Player player);
}
