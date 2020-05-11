package com.malt.aster.activities;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * Helper class that can be used to define a phase in an activity, e.g. looking for users,
 * in progress, or finishing up. A condition is evaluated continually by the activity manager after each time a
 * reaction or message event occurs.
 */
public interface ActivityPhase {

    /**
     * Decide what happens when the activity starts
     */
    void onStart();

    /**
     * Decide what happens when a message arrives
     * @param evt The relevant {@link GuildMessageReceivedEvent}
     */
    void handleMessage(GuildMessageReceivedEvent evt);

    /**
     * Decide what happens when a reaction is added
     *
     * @param evt The relevant {@link GuildMessageReactionAddEvent}
     */
    void handleReaction(GuildMessageReactionAddEvent evt);

    /**
     * Decide what happens when a private message (direct message) arrives
     *
     * @param evt The relevant {@link PrivateMessageReceivedEvent}
     */
    void handlePrivateMessage(PrivateMessageReceivedEvent evt);

}
