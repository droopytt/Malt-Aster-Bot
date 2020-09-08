package com.malt.aster.activities.uno.cards;

public enum CardAction {
    DRAW_TWO (20),
    SKIP (20),
    REVERSE (20),
    WILD (50),
    WILD_DRAW_FOUR(50);

    int scoreValue;
    CardAction(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    int getScoreValue() {
        return scoreValue;
    }
}
