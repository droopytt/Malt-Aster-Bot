package com.malt.aster.activities.uno;

import com.malt.aster.utils.Constants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.malt.aster.utils.Constants.*;

/**
 * Deals with the part of uno that handles the recruitment process at the start of the game
 */
public class UnoStartPhase extends UnoPhase {

    Message unoMessage;

    public UnoStartPhase(Uno uno) {
        super(uno);
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {}

    @Override
    public void onStart() {
        // Send the initial message allowing users to join and the commander to start the game
        // using reactions

        unoMessage = uno.getOriginalEvent()
                .getChannel()
                .sendMessage("UNO started! React to this message"
                        + " with a :white_check_mark: to join, the game will begin when "
                        + uno.getCommander().getAsMention()
                        + " reacts with an :negative_squared_cross_mark:")
                .complete();

        unoMessage.addReaction(Constants.CHECK_EMOTE).queue();
        unoMessage.addReaction(Constants.CROSS_EMOTE).queue();
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {
        String emote = evt.getReaction().getReactionEmote().getName();
        User commander = uno.getCommander();

        if (evt.getUser().equals(commander) && emote.equalsIgnoreCase(CROSS_EMOTE)) {

            // Add commander and anyone who reacted with the checkmark to the list of participants
            participants.add(commander);

            // Need stream supplier to reuse stream since streams are closed after you run a terminal operation like
            // count below.
            Supplier<Stream<User>> userStreamSupplier =
                    () -> unoMessage.retrieveReactionUsers(CHECK_EMOTE).stream().filter(user -> !user.isBot());

            if (userStreamSupplier.get().count() >= MINIMUM_UNO_PLAYERS) {
                userStreamSupplier.get().filter(user -> !user.equals(commander)).forEach(participants::add);

                // Display the users who are to participate
                StringBuilder stringBuilder = new StringBuilder();

                stringBuilder.append("The current participants are: \n");
                participants.forEach(
                        user -> stringBuilder.append(user.getAsMention()).append("\n"));
                stringBuilder.append(
                        "You have been messaged your cards, and the game will now proceed in private messages.");

                // Update the messages: Delete the original message, and now update uno.
                evt.getChannel().sendMessage(stringBuilder.toString().trim()).queue(callback -> {
                    uno.update();
                    unoMessage.delete().queue();
                });
            } else {
                // Disband the session if the session does not have enough players
                unoMessage
                        .editMessage(unoMessage.getContentRaw() + "\nYou must have at least " + MINIMUM_UNO_PLAYERS
                                + " players to start - Your session has been disbanded")
                        .queue();

                uno.cleanUp();
            }
        }
    }

    @Override
    public void handlePrivateMessage(PrivateMessageReceivedEvent evt) {}
}
