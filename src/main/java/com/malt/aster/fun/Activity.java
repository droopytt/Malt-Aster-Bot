package com.malt.aster.fun;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

/**
 * A generic activity. Can be a minigame that has multiple participating players, or a single player
 * activity. An activity has a "commander" (the user who instantiated the activity).
 */
public interface Activity {

    void startUp(GuildMessageReceivedEvent evt);

    void handleMessage(GuildMessageReceivedEvent evt);

    void handleReaction(GuildMessageReactionAddEvent evt);

    User getCommander();

    ActivityType getType();
}
