package com.malt.aster.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SingleParamCommand extends Command {

    public SingleParamCommand(String name) {
        super(name);
    }

    @Override
    public void execute(GuildMessageReceivedEvent evt, List<String> params) {
        if(params.isEmpty())
            execute(evt, (String) null);
        else {
            StringBuilder stringBuilder = new StringBuilder();
            params.forEach(stringBuilder::append);
            execute(evt, stringBuilder.toString());
        }

    }

    /**
     * Executes the command given the context, but does not use a list of parameters.
     * Params may be null.
     *
     * @param evt The event to execute according to
     * @param params The parameter strings passed aside from the command text, not in list format. Can be null.
     */
    public abstract void execute(GuildMessageReceivedEvent evt, @Nullable String params);
}
