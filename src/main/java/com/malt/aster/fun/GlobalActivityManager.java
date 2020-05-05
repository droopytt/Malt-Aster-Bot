package com.malt.aster.fun;

import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to obtain the {@link GuildActivityManager} for a particular guild
 * @see GuildActivityManager
 */
public class GlobalActivityManager {

    private final Map<String, GuildActivityManager> managersForGuilds;

    public GlobalActivityManager() {
        managersForGuilds = new HashMap<>();
    }

    public GuildActivityManager getActivityManagerForGuild(Guild guild) {
        return getActivityManagerForGuild(guild.getId());
    }

    public GuildActivityManager getActivityManagerForGuild(String guildId) {
        GuildActivityManager manager = managersForGuilds.get(guildId);

        if(manager == null) {
            manager = new GuildActivityManager(Bot.getInstance().getBotUser().getGuildById(guildId));
            managersForGuilds.put(guildId, manager);
        }

        return manager;
    }

    public void handleReaction(GuildMessageReactionAddEvent event) {
        getActivityManagerForGuild(event.getGuild()).handleReaction(event);
    }
}
