package com.malt.aster.activities;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 * A generic activity. Can be a minigame that has multiple participating players, or a single player
 * activity. An activity has a "commander" (the user who instantiated the activity).
 */
public interface Activity {

    /**
     * Decide what to do when the event starts up.
     * This should be called by the command that instantiates the activity after doing any checks.
     * @param evt The event data to use to start the activity
     */
    void startUp(GuildMessageReceivedEvent evt);

    /**
     * Decide what happens when a message arrives
     * @param evt The relevant {@link GuildMessageReceivedEvent}
     */
    void handleMessage(GuildMessageReceivedEvent evt);

    /**
     * Decide what happens when a reaction is added
     * @param evt The relevant {@link GuildMessageReactionAddEvent}
     */
    void handleReaction(GuildMessageReactionAddEvent evt);

    /**
     * @return The {@link User} who instantiated the activity
     */
    User getCommander();

    /**
     * The activity type
     * @return {@link ActivityType} for this activity
     */
    ActivityType getType();
}
