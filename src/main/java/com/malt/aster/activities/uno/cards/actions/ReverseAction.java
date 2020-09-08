package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.User;

public class ReverseAction implements UnoActionable {
    @Override
    public void perform(final User sender, final UnoMainGame unoMainGame) {
        unoMainGame.reverse();
        unoMainGame.getParticipants().forEach(user -> user.openPrivateChannel()
                .queue(channel -> channel.sendMessage(Utils.getEffectiveName(user, unoMainGame.getGuild()) + " has reversed the turn order!").queue()));
    }
}
