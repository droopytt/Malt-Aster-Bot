package com.malt.aster.core;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

/**
 * Deals with a lot of the events, for example, text messages incoming, users leaving and joining the server.
 * There's a lot of methods to override in here. See documentation for ListenerAdapter or find the key
 * that lets you see the methods you can override in your IDE.
 */
public class EventHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot())
            return;

        if(containsCommand(event))
            Bot.getInstance().handleCommandEvent(event);

        Bot.getInstance().getActivityManager().handleMessage(event);
    }

    private boolean containsCommand(GuildMessageReceivedEvent event) {
        return event.getMessage().getContentRaw().startsWith(Bot.getInstance().getPrefix());
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        Bot.getInstance().getActivityManager().handleReaction(event);
    }
}
