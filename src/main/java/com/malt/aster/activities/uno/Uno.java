package com.malt.aster.activities.uno;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.ActivityPhase;
import com.malt.aster.activities.ActivityType;
import com.malt.aster.utils.Constants;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Uno implements Activity {

    private final User commander;

    Message startMessage;

    private final Set<User> participants;

    private boolean started;

    private ActivityPhase currentActivityPhase;

    private final Queue<ActivityPhase> phases;

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
        participants = new HashSet<>();
        phases = new LinkedBlockingQueue<>();
    }

    @Override
    public void startUp(GuildMessageReceivedEvent evt) {
        if(!started) {
            //Send the initial message allowing users to join and the commander to start the game
            //using reactions
            startMessage = evt.getChannel().sendMessage("UNO started! React to this message"
                    + " with a CHECK to join, the game will begin when " +
                    commander.getAsMention() + " reacts with an 'X'").complete();

            startMessage.addReaction(Constants.CHECK_EMOTE).queue();
            startMessage.addReaction(Constants.CROSS_EMOTE).queue();
            started = true;

            // Add all the phases for this activity
            addPhase(new UnoStartPhase(this));
            addPhase(new UnoMainGame(this));

            update();
        }
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        currentActivityPhase.handleMessage(evt);
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {
        currentActivityPhase.handleReaction(evt);
    }

    @Override
    public User getCommander() {
        return commander;
    }

    @Override
    public ActivityType getType() {
        return ActivityType.UNO;
    }

    private void addPhase(ActivityPhase phase) {
        phases.add(phase);
    }

    public void update() {
        if(phases.isEmpty())
            cleanUp();
        else
            currentActivityPhase = phases.poll();
    }

    Set<User> getParticipants() {
        return Collections.unmodifiableSet(participants);
    }

    @Override
    public void cleanUp() {
        System.out.println("Uno@cleanUp: Cleaning up...");
        // TODO implement
    }
}
