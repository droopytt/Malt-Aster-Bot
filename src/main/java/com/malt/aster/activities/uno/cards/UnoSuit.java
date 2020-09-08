package com.malt.aster.activities.uno.cards;

import java.awt.*;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

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

    // Returns a random suit that isn't a wild suit
    public static UnoSuit getRandomColouredSuit() {
        UnoSuit[] arrSuits = UnoSuit.values();
        java.util.List<UnoSuit> suits = Arrays.stream(arrSuits).filter(suit -> suit != UnoSuit.WILD).collect(Collectors.toList());
        return suits.get(new Random().nextInt(suits.size()));
    }
}
