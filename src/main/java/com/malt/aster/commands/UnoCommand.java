package com.malt.aster.commands;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.uno.Uno;
import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class UnoCommand extends Command {
    public UnoCommand() {
        super("uno");
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, List<String> params) {
        if (params.size() == 0)
            evt.getChannel().sendMessage("Need to set gambling value... WIP").queue();
        else {
            Activity unoActivity = new Uno(evt);

            // Register the activity with the guild activity manager. If it could be added that means we start the
            // activity.
            if (Bot.getInstance()
                    .getActivityManager()
                    .getActivityManagerForGuild(evt.getGuild())
                    .addActivity(unoActivity)) unoActivity.onStart();
        }
    }

    @Override
    public String getDescription() {
        return "Starts and runs a game of UNO";
    }
}
