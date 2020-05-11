package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.ColoredCard;

import java.awt.*;

public class ValuedUnoCard extends UnoCard implements ColoredCard {

    private final int value;

    private final Color color;


    public ValuedUnoCard(int value, Color color) {
        super();
        this.color = color;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "ValuedUnoCard{" +
                "value=" + value +
                ", color=" + color +
                '}';
    }
}
