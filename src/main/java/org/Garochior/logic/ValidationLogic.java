package org.Garochior.logic;

import org.Garochior.model.Card;

import java.util.List;

public abstract class ValidationLogic implements GameLogic{
    public List<Card> selectedCards;
    //ceva sa selectam cartile, pe care le adaugam in lista, ca sa stim ordinea lor, prima carte mereu fiind a jucatorului care a inceput tura
    @Override
    public boolean isValidMove() {
        return false;
    }
}
