package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.cards.Card;

public abstract class UnoCard implements Card {

    protected UnoSuit suit;

    public UnoCard(UnoSuit suit) {
        this.suit = suit;
    }

    /**
     * @return Whether this card is a value card
     */
    public boolean isValued() {
        return this instanceof ValuedUnoCard;
    }

    public boolean isAction() {
        return this instanceof ActionUnoCard;
    }

    public UnoSuit getSuit() {
        return suit;
    }

    @Override
    public String getImageUrl() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public abstract int getScoreValue();

    @Override
    public String toString() {
        return "UnoCard{" + "suit=" + suit + '}';
    }
}
