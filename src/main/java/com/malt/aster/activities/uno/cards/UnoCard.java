package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.cards.ValuedCard;

public abstract class UnoCard implements Card {

    public UnoCard() {

    }

    /**
     * @return Whether this card is a value card
     */
    public boolean isValued() {
        return this instanceof ValuedCard;
    }

    @Override
    public String getImageUrl() {
        // TODO implement
        return null;
    }
}
