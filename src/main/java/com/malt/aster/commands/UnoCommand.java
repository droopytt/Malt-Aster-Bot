package com.malt.aster.commands;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.uno.Uno;
import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

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
        	// TODO make this return false if the activity could not be added and don't startup if that was the case

            // Register the activity with the guild activity manager
            Bot.getInstance().getActivityManager().getActivityManagerForGuild(evt.getGuild()).addActivity(unoActivity);
        	unoActivity.onStart();
        }
    }

    
    @Override
    public String getDescription() {
        return "Starts and runs a game of UNO";
    }
}
