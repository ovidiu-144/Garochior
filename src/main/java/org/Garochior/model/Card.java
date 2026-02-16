package org.Garochior.model;
import org.Garochior.constants.ModelConfig;

public class Card implements CardInterface{
    private final int number;
    private final CardType type;

    public Card(int number, CardType type) {
        this.number = number;
        this.type = type;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        return number == card.number && type == card.type;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public String toString() {
        String value = switch (number){
            case ModelConfig.A -> "A";
            case ModelConfig.K -> "K";
            case ModelConfig.Q -> "Q";
            case ModelConfig.J -> "J";
            default -> Integer.toString(number);
        };
        return "(" + value + ", " + type + ")";
    }
}
