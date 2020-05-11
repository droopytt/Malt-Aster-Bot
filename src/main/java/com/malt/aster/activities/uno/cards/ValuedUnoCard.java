package com.malt.aster.activities.uno.cards;

/**
 * An uno card with some numerical value attached to it
 */
public class ValuedUnoCard extends UnoCard {

    private final int value;

    private final UnoSuit suit;


    public ValuedUnoCard(int value, UnoSuit suit) {
        super();
        this.suit = suit;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public UnoSuit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return suit + " " + value;
    }
}
