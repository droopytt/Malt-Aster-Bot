package com.malt.aster.activities.uno.cards;

/**
 * An uno card with some numerical value attached to it
 */
public class ValuedUnoCard extends UnoCard {

    private final int value;

    public ValuedUnoCard(int value, UnoSuit suit) {
        super(suit);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String suitString = suit.toString();
        return suitString.charAt(0) + suitString.substring(1).toLowerCase() + " " + value;
    }
}
