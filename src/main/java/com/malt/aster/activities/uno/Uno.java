package com.malt.aster.activities.uno;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.ActivityPhase;
import com.malt.aster.activities.ActivityType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class Uno implements Activity {

    private final User commander;

    Message unoMessage;

    GuildMessageReceivedEvent originalEvent;

    private final Set<User> participants;

    private boolean started;

    private ActivityPhase currentActivityPhase;

    private final Queue<ActivityPhase> phases;

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
        participants = new HashSet<>();
        phases = new LinkedBlockingQueue<>();
        originalEvent = event;
    }

    @Override
    public void onStart() {
        if(!started) {
            unoMessage = originalEvent.getMessage();

            // Add all the phases for this activity
            addPhase(new UnoStartPhase(this));
            addPhase(new UnoMainGame(this));

            update();

            started = true;
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
        else {
            currentActivityPhase = phases.poll();
            currentActivityPhase.onStart();
        }
    }

    Set<User> getParticipants() {
        return participants;
    }

    @Override
    public void cleanUp() {
        System.out.println("Uno@cleanUp: Cleaning up...");
        // TODO implement
    }
}
