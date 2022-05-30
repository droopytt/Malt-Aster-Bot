package com.malt.aster.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.*;

public final class CommandManager {

    private final Map<String, Command> commands;

    public CommandManager() {
        commands = new HashMap<>();
    }

    /*
    Deals with a command, given a command name and the context (the event)
     */
    public void handleCommand(String commandName, GuildMessageReceivedEvent event) {

        // Adds any space separated strings to the parameter list
        commandFromName(commandName).ifPresent(command -> {
            // Check if the command is an administrator command
            if (!(command instanceof AdminCommand) || event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                String[] tokens = event.getMessage().getContentRaw().substring(1).split(" ", 2);
                List<String> paramList = new ArrayList<>();
                if (hasParams(tokens)) {
                    final String params = tokens[1].trim();
                    paramList = new ArrayList<>(Arrays.asList(params.split(" ")));
                }
                command.execute(event, paramList);
            }

        });
    }

    /**
     * Registers a command to this command manager, and any of its aliases
     * @param command The command to add to the command manager
     */
    public CommandManager register(Command command) {
        commands.put(command.getName().toLowerCase(), command);

        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }

        return this;
    }

    private boolean hasParams(String[] tokens) {
        return tokens.length > 1;
    }

    /**
     * Returns an Optional. Saves having to do null checks when the command manager handles this
     */
    private Optional<Command> commandFromName(String name) {
        return Optional.ofNullable(commands.get(name));
    }
}
