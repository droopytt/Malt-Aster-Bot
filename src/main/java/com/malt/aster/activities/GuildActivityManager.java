package com.malt.aster.activities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the activities for a particular {@link Guild} (server)
 */
public class GuildActivityManager {

    // The guild this activity manager belongs to
    private Guild guild;

    // Mappings from the user who created the activity (in this context, the "commander") and the activity they initiated
    private Map<User, Activity> activities;

    public GuildActivityManager(Guild guild) {
        this.guild = guild;
        activities = new HashMap<>();
    }

    /**
     * Adds an activity to this manager.
     * <p>
     * If the user who has created an activity already has an activity in progress, then they will be notified
     * that the activity could not be started via direct message.
     *
     * @param activity The {@link Activity} to register
     * @return true if the activity was added successfully, false otherwise
     */
    public boolean addActivity(Activity activity) {
        if(activities.containsKey(activity.getCommander())) {
            notifyActivityStartupFailed(activity);
            return false;
        } else {
            activities.put(activity.getCommander(), activity);
            return true;
        }
    }

    public void removeActivity(Activity activity) {
        if(activities.containsKey(activity.getCommander()))
            activities.remove(activity.getCommander());
        else
            throw new IllegalStateException("The commander currently does not have an activity to remove.");
    }

    public void handleReaction(GuildMessageReactionAddEvent event) {
        Activity activity = activities.get(event.getUser());
        if (activity != null)
            activity.handleReaction(event);
    }

    public void handleMessage(GuildMessageReceivedEvent event) {
        Activity activity = activities.get(event.getAuthor());
        if (activity != null)
            activity.handleMessage(event);
    }

    public void handlePrivateMessage(PrivateMessageReceivedEvent event) {
        Activity activity = activities.get(event.getAuthor());
        if (activity != null)
            activity.handlePrivateMessage(event);
    }

    /**
     * Used to notify the activity commander that they already have an activity in progress
     *
     * @param activity The activity that failed to start
     */
    private void notifyActivityStartupFailed(Activity activity) {
        PrivateChannel directMessageChannel = activity.getCommander().openPrivateChannel().complete();
        String activityName = activity.getType().toString().toLowerCase();

        directMessageChannel.sendMessage("Your activity " + activityName +
                        " could not be started because you already have one in progress.").queue();
    }

}
