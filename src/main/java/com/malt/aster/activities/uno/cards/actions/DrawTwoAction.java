package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.User;

import static com.malt.aster.utils.Utils.getEffectiveName;

public class DrawTwoAction implements UnoActionable {
    @Override
    public void perform(User sender, UnoMainGame unoMainGame) {
        User nextPlayer = unoMainGame.getNextPlayer();

        unoMainGame.getParticipants().forEach(participant -> participant
                .openPrivateChannel()
                .queue(channel -> channel.sendMessage(getEffectiveName(nextPlayer, unoMainGame.getGuild())
                                + " has drawn 2 cards and had their turn skipped!")
                        .queue()));

        unoMainGame.draw(nextPlayer, 2);
        unoMainGame.updatePlayerIndex();
        unoMainGame.nextTurn();
    }
}
