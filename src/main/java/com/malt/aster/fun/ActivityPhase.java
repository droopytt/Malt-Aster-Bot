package com.malt.aster.fun;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 * Helper class that can be used to define a phase in an activity, e.g. looking for users,
 * in progress, or finishing up. A condition is evaluated continually by the activity manager after each time a
 * reaction or message event occurs.
 */
public interface ActivityPhase {

    void handleMessage(GuildMessageReceivedEvent evt);

    void handleReaction(GuildMessageReactionAddEvent evt);

    // Returns condition(s) that are required for the completion of this activity phase
    boolean canProgress();

}
