package com.malt.aster.activities.uno;

import com.malt.aster.utils.Constants;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Objects;

import static com.malt.aster.utils.Constants.CHECK_EMOTE;
import static com.malt.aster.utils.Constants.CROSS_EMOTE;

/**
 * Deals with the part of uno that handles the recruitment process at the start of the game
 */
public class UnoStartPhase extends UnoPhase {

    public UnoStartPhase(Uno uno) {
        super(uno);
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {}

    @Override
    public void onStart() {
        //Send the initial message allowing users to join and the commander to start the game
        //using reactions

        Message startMessage = uno.originalEvent.getChannel().sendMessage("UNO started! React to this message"
                + " with a CHECK to join, the game will begin when " +
                uno.getCommander().getAsMention() + " reacts with an 'X'").complete();

        startMessage.addReaction(Constants.CHECK_EMOTE).queue();
        startMessage.addReaction(Constants.CROSS_EMOTE).queue();
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {
        String emote = evt.getReaction().getReactionEmote().getName();
        User commander = uno.getCommander();

        if(evt.getUser().equals(commander) && emote.equalsIgnoreCase(CROSS_EMOTE)) {

            // Add commander and anyone who reacted with the checkmark to the list of participants
            participants.add(commander);

            startUno.retrieveReactionUsers(CHECK_EMOTE).stream()
                    .filter(user -> !user.isBot())
                    .filter(user -> !user.equals(commander))
                    .forEach(participants::add);

            // Display the users who are to participate
            StringBuilder stringBuilder = new StringBuilder();
            Guild guild = evt.getGuild();

            stringBuilder.append("The current participants are: \n");
            participants.forEach(user -> stringBuilder.append(Objects.requireNonNull(guild.getMember(user)).getEffectiveName()).append("\n"));

            evt.getChannel().sendMessage(stringBuilder.toString().trim()).queue(callback -> uno.update());
        }
    }
}
