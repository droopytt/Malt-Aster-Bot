package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import net.dv8tion.jda.api.entities.User;

public interface UnoActionable {
    public void perform(final User sender, final UnoMainGame unoMainGame);
}
