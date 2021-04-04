package com.malt.aster.activities.uno.cards;

import java.awt.*;

public enum UnoSuit {
    RED(Color.RED),
    GREEN(Color.GREEN),
    BLUE(Color.BLUE),
    YELLOW(Color.YELLOW),
    WILD(Color.BLACK);

    Color color;

    UnoSuit(Color color) {
        this.color = color;
    }

}
