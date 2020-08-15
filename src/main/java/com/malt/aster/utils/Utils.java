package com.malt.aster.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.Objects;

public class Utils {

    public static String getEffectiveName(User user, Guild guild) {
        return Objects.requireNonNull(guild.getMember(user), "No member found for this user in that guild").getEffectiveName();
    }
}
