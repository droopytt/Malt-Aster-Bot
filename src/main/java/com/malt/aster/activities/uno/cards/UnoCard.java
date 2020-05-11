package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.Card;
import com.malt.aster.activities.ValuedCard;

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
