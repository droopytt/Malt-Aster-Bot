package com.malt.aster.core;

import com.malt.aster.activities.GlobalActivityManager;
import com.malt.aster.commands.CommandManager;
import com.malt.aster.commands.LatencyCommand;
import com.malt.aster.commands.SetNickCommand;
import com.malt.aster.commands.UnoCommand;
import com.malt.aster.commands.utility.CheckRolesCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.security.auth.login.LoginException;

/**
 * The bot singleton. Has some methods that let you obtain the bot, e.g. getBotUser which returns the bot itself
 */
public class Bot {

    public static final String PREFIX = "!";
    private static Bot instance;

    private JDA botUser;
    private CommandManager commandManager;
    private GlobalActivityManager activityManager;

    private Bot(String token) throws LoginException {
        botUser = new JDABuilder(token).addEventListeners(new EventHandler()).build();

        commandManager = new CommandManager();
        activityManager = new GlobalActivityManager();

        installCommands();
    }

    /**
     * Register commands here to install into the bot.
     *
     * Simply create a class that extends the Command abstract class, and implement the
     * execute method. Then register it here and the bot will listen out for it.
     *
     * Can be used as a chain of calls, for API ease of use.
     */
    private void installCommands() {
        commandManager
                .register(new LatencyCommand())
                .register(new SetNickCommand())
                .register(new CheckRolesCommand())
                .register(new UnoCommand());
    }

    public static void init(String token) throws LoginException {
        if (instance != null) {
            throw new IllegalStateException("Bot has already been initialised.");
        }
        instance = new Bot(token);
    }

    public void handleCommandEvent(GuildMessageReceivedEvent event) {
        // If the event message is, e.g. !cmd testing testing, commandName is set to "cmd"
        String commandName =
                event.getMessage().getContentRaw().substring(1).split(" ")[0].toLowerCase();
        commandManager.handleCommand(commandName, event);
    }

    public static Bot getInstance() {
        if (instance == null)
            throw new IllegalStateException("Bot has not been initialised. Please use Bot#init() to create the bot");
        return instance;
    }

    public String getPrefix() {
        return PREFIX;
    }

    public JDA getBotUser() {
        return botUser;
    }

    public GlobalActivityManager getActivityManager() {
        return activityManager;
    }
}
