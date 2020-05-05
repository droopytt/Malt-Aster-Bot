package com.malt.aster.commands;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.malt.aster.core.Bot;
import com.malt.aster.fun.Activity;
import com.malt.aster.fun.uno.Uno;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;

import java.util.List;
import java.util.Objects;

public class UnoCommand extends Command implements AdminCommand {
    public UnoCommand() {
        super("uno");
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, List<String> params) {
        if(params.size() == 0)
            evt.getChannel().sendMessage("Need to set gambling value... WIP").queue();
        else {
        	Activity unoActivity = new Uno(evt);
            Bot.getInstance().getActivityManager().getActivityManagerForGuild(evt.getGuild()).addActivity(unoActivity);
        	unoActivity.startUp(evt);
        }
    }

    
    @Override
    public String getDescription() {
        return "Starts and runs a game of UNO";
    }
}
