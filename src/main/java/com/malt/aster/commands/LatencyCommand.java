package com.malt.aster.commands;

import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class LatencyCommand extends Command {

    public LatencyCommand() {
        super("ping");
        addAlias("latency").addAlias("pong");
    }

    @Override
    public void execute(GuildMessageReceivedEvent event, List<String> params) {
        event.getChannel()
                .sendMessage("The current latency between me and the discord servers is "
                        + Bot.getInstance().getBotUser().getGatewayPing())
                .queue();
    }

    @Override
    public String getDescription() {
        return "Returns latency between the discord bot and the official discord servers";
    }
}
