package com.malt.aster.fun.uno;

import com.malt.aster.fun.Activity;
import com.malt.aster.fun.ActivityType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;

public class Uno implements Activity {

    private static final String CHECK_EMOTE = "\u2705";
    private static final String CROSS_EMOTE = "\u274E";
    private final User commander;

    private Message startUno;

    private boolean started;

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
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
            evt.getChannel().sendMessage("jsjsjasjasjjsak").queue();

            startUno.retrieveReactionUsers(CHECK_EMOTE)
                    .forEach(x -> evt.getChannel().sendMessage(x.getName()).queue());
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
