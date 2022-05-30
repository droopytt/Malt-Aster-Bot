package com.malt.aster.activities.uno.cards;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.activities.uno.cards.actions.UnoActionHandler;
import com.malt.aster.activities.uno.cards.actions.UnoActionable;
import net.dv8tion.jda.api.entities.User;

public enum CardAction implements UnoActionable {
    DRAW_TWO(20),
    SKIP(20),
    REVERSE(20),
    WILD(50),
    WILD_DRAW_FOUR(50);

    int scoreValue;

    CardAction(int scoreValue) {
        this.scoreValue = scoreValue;
    }

    int getScoreValue() {
        return scoreValue;
    }

    public void perform(User sender, UnoMainGame unoMainGame) {
        UnoActionHandler.getInstance().handle(sender, this, unoMainGame);
    }
}
