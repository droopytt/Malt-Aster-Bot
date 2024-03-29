package com.malt.aster.commands;

import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;

/**
 * To make a new command there are a few things to do:
 * <p><p>
 * 1) Make a class that extends this Class<p>
 * 2) Implement ONE of the execute methods of the class (it's abstract in here) See LatencyCommand and SetName command for example <p>
 * 3) Add the command to the Bot under the installCommands method
 * <p><p>
 * Do NOT implement both execute methods unless you want both implementations to happen.
 *
 * @see Bot
 */
public abstract class Command {

    protected String name;
    protected List<String> parameters;
    protected Set<String> aliases;

    public Command(String name) {
        this.name = name;
        this.parameters = new ArrayList<>(3);
        this.aliases = new HashSet<>();
    }

    public Command addAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    /**
     * Executes the command given the context (event)
     *
     * @param evt    The event context to execute according to
     * @param params Potential parameters for this command execution
     */
    public abstract void execute(GuildMessageReceivedEvent evt, List<String> params);

    public String getName() {
        return name;
    }

    /**
     * Returns a description for this command. Can be used in commands that describe the commands.
     *
     * @return A string corresponding to a description of this command
     */
    public abstract String getDescription();

    public List<String> getParameters() {
        return unmodifiableList(parameters);
    }

    /**
     * @return A set of aliases of this command
     */
    public Set<String> getAliases() {
        return unmodifiableSet(aliases);
    }
}
