package com.malt.aster.activities;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * A generic activity. Can be a minigame that has multiple participating players, or a single player
 * activity. An activity has a "commander" (the user who instantiated the activity).
 */
public interface Activity extends ActivityPhase {

    /**
     * Decide what to do when the event starts up.
     * This should be called by the command that instantiates the activity after doing any checks.
     * @param evt The event data to use to start the activity
     */
    void startUp(GuildMessageReceivedEvent evt);

    /**
     * @return The {@link User} who instantiated the activity
     */
    User getCommander();

    /**
     * The activity type
     * @return {@link ActivityType} for this activity
     */
    ActivityType getType();

    /**
     * Used to define what happens when this activity finishes
     */
    void cleanUp();
}
