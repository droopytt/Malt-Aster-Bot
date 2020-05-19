package com.malt.aster.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Used to represent a command that wants to manipulate the parameters of the command without treating them
 * as a list. For example, for the following command invocation:
 * <p><p>
 * !foo bar1 bar2 bar3
 * <p><p>
 * The regular {@link Command} treats it as:
 * List={bar1, bar2, bar3}
 * <p>
 * However, {@link SingleParamCommand} treats them all as one single string:
 * bar1 bar2 bar3
 * <p> <p>
 * This might be useful in some scenarios for more fine-tuned manipulation.
 * <p><p>
 * For example,
 * for the {@link SetNickCommand}, the name to set can have spaces. So to save the need to call
 * code like this:
 * <p><p>
 * <code>
 * StringBuilder stringBuilder = new StringBuilder();
 * params.forEach(str -> stringBuilder.append(str).append(" "));
 * </code>
 * <p><p>
 * This class {@link SingleParamCommand#execute(GuildMessageReceivedEvent, List)} simply does that call each time anyway.
 * and passes the processed arguments to {@link SingleParamCommand#execute(GuildMessageReceivedEvent, String)}
 */
public abstract class SingleParamCommand extends Command {

    public SingleParamCommand(String name) {
        super(name);
    }

    /**
     * Executes the command given the context, but does not use a list of parameters.
     * Params may be null.
     *
     * @param evt    The event to execute according to
     * @param params The parameter strings passed aside from the command text, not in list format. Can be null.
     */
    public abstract void execute(GuildMessageReceivedEvent evt, @Nullable String params);

    /**
     * Calls this command but collates all parameters into one single word, rather than keeping
     * space-separated strings as parameters.
     *
     * @param evt    The event context to execute according to
     * @param params Potential parameters for this command execution
     */
    @Override
    public final void execute(GuildMessageReceivedEvent evt, List<String> params) {
        if (params.isEmpty())
            execute(evt, (String) null);
        else {
            StringBuilder stringBuilder = new StringBuilder();
            params.forEach(str -> stringBuilder.append(str).append(" "));
            execute(evt, stringBuilder.toString().trim());
        }

    }
}
