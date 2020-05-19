package com.malt.aster.commands;

import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;

/**
 * Sets the name of the bot in the server it is called in.
 * <p>
 * Does not change the global username of the bot.
 */
public class SetNickCommand extends SingleParamCommand implements AdminCommand {

    public SetNickCommand() {
        super("setnick");
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, @Nullable String params) {
        if(params == null)
            evt.getChannel().sendMessage("You need to provide a name to change to").queue();
        else {
            String botId = Bot.getInstance().getBotUser().getSelfUser().getId();
            evt.getGuild().getMemberById(botId).modifyNickname(params).queue();
        }
    }

    @Override
    public String getDescription() {
        return "Changes the name of the bot in the server it is called in";
    }
}
