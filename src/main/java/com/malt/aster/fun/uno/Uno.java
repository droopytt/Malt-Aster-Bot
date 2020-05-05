package com.malt.aster.fun.uno;

import com.malt.aster.fun.Activity;
import com.malt.aster.fun.ActivityType;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class Uno implements Activity {

    private User commander;

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        // TODO Decide what to do when a message is received
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {

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
