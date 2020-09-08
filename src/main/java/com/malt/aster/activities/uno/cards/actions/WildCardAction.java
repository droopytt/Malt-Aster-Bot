package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import net.dv8tion.jda.api.entities.User;

public class WildCardAction implements UnoActionable {
    @Override
    public void perform(User sender, UnoMainGame unoMainGame) {
        // TODO implement
        unoMainGame.nextTurn();
    }
}
