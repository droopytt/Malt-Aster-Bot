package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.User;

public class SkipAction implements UnoActionable {
    @Override
    public void perform(User sender, UnoMainGame unoMainGame) {
        String skippedParticipantName = Utils.getEffectiveName(unoMainGame.getNextPlayer(), unoMainGame.getGuild());

        unoMainGame.getParticipants().forEach(user -> user.openPrivateChannel()
                .queue(channel -> channel.sendMessage(skippedParticipantName + " has had their turn skipped!").queue()));
        unoMainGame.updatePlayerIndex();
    }
}
