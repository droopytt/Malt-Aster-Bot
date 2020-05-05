package com.malt.aster.activities.uno;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.ActivityType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashSet;
import java.util.Set;

public class Uno implements Activity {

    private static final String CHECK_EMOTE = "\u2705";
    private static final String CROSS_EMOTE = "\u274E";
    private final User commander;

    private Message startUno;

    private Set<User> participants;

    private boolean started;

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
        participants = new HashSet<>();
    }

    @Override
    public void startUp(GuildMessageReceivedEvent evt) {
        if(!started) {
            //Send the initial message allowing users to join and the commander to start the game
            //using reactions
            startUno = evt.getChannel().sendMessage("UNO started! React to this message"
                    + " with a CHECK to join, the game will begin when " +
                    commander.getAsMention() + " reacts with an 'X'").complete();

            startUno.addReaction(CHECK_EMOTE).queue();
            startUno.addReaction(CROSS_EMOTE).queue();

            started = true;
        }
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        // TODO Decide what to do when a message is received
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {
        String emote = evt.getReaction().getReactionEmote().getName();

        if(evt.getUser().equals(commander) && emote.equalsIgnoreCase(CROSS_EMOTE)) {

            // Add commander and anyone who reacted with the checkmark to the list of participants
            participants.add(commander);

            startUno.retrieveReactionUsers(CHECK_EMOTE).stream()
                    .filter(user -> !user.isBot())
                    .filter(user -> !user.equals(commander))
                    .forEach(user -> participants.add(user));

            // Display the users who are to participate
            StringBuilder stringBuilder = new StringBuilder();
            Guild guild = evt.getGuild();

            stringBuilder.append("The current participants are: \n");
            participants.forEach(user -> stringBuilder.append(guild.getMember(user).getEffectiveName()).append("\n"));

            evt.getChannel().sendMessage(stringBuilder.toString().trim()).queue();
        }
    }

    @Override
    public User getCommander() {
        return commander;
    }

    @Override
    public ActivityType getType() {
        return ActivityType.UNO;
    }
}
