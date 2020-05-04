package com.malt.aster.commands;

import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

/**
 * Sets the name of the bot in the server it is called in
 */
public class SetNameCommand extends Command implements AdminCommand {

    public SetNameCommand() {
        super("setnick");
        addAlias("localname");
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, List<String> params) {
        if(params.size() == 0)
            event.getChannel().sendMessage("You need to provide a name to change to").queue();
        else {
            String newName = params.get(0);
            String botId = Bot.getInstance().getBotUser().getSelfUser().getId();

            event.getGuild().getMemberById(botId).modifyNickname(newName).queue();
        }
    }

    @Override
    public String getDescription() {
        return "Changes the name of the bot in the server it is called in";
    }
}
