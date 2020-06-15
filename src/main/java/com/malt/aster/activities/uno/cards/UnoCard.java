package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.cards.ValuedCard;

public abstract class UnoCard implements Card {

    protected UnoSuit suit;

    public UnoCard(UnoSuit suit) {
        this.suit = suit;
    }

    /**
     * @return Whether this card is a value card
     */
    public boolean isValued() {
        return this instanceof ValuedCard;
    }

    public UnoSuit getSuit() {
        return suit;
    }

    @Override
    public String getImageUrl() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "UnoCard{" +
                "suit=" + suit +
                '}';
    }
}
