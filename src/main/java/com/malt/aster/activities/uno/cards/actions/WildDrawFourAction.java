package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.activities.uno.cards.UnoSuit;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.User;

import java.util.Arrays;

public class WildDrawFourAction implements UnoActionable {
    @Override
    public void perform(User sender, UnoMainGame unoMainGame) {
        StringBuilder stringBuilder = new StringBuilder(Utils.getEffectiveName(sender, unoMainGame.getGuild())
                + " has put down a wild draw 4. Please wait for them to select the suit.");
        notifyWildCardToOtherParticipants(sender, unoMainGame, stringBuilder);
    }

    static void notifyWildCardToOtherParticipants(User sender, UnoMainGame unoMainGame, StringBuilder stringBuilder) {
        unoMainGame.getParticipants().stream()
                .filter(user -> !user.equals(sender))
                .forEach(user -> user.openPrivateChannel()
                        .queue(channel -> channel.sendMessage(stringBuilder).queue()));

        stringBuilder.append("\n\nPlease pick a suit name from the following\n");
        Arrays.stream(UnoSuit.values())
                .filter(unoSuit -> unoSuit != UnoSuit.WILD)
                .forEach(suit ->
                        stringBuilder.append(suit.toString().toLowerCase()).append("\n"));
        sender.openPrivateChannel()
                .queue(privateChannel ->
                        privateChannel.sendMessage(stringBuilder.toString()).queue());
        unoMainGame.setWaitingOnWild();
    }
}
