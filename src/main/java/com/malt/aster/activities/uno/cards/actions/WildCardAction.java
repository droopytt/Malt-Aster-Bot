package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.activities.uno.cards.UnoSuit;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class WildCardAction implements UnoActionable {
    @Override
    public void perform(User sender, UnoMainGame unoMainGame) {
        StringBuilder stringBuilder = new StringBuilder(Utils.getEffectiveName(sender, unoMainGame.getGuild()) + " has put down a wildcard. Please wait for them to select the suit.");
        WildDrawFourAction.notifyWildCardToOtherParticipants(sender, unoMainGame, stringBuilder);
    }
}
