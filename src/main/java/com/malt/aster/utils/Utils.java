package com.malt.aster.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static java.util.Objects.requireNonNull;

public class Utils {

    public static String getEffectiveName(User user, Guild guild) {
        return requireNonNull(guild.getMember(user), "No member found for this user in that guild")
                .getEffectiveName();
    }

    public static void shuffleCollection(List<?> coll, int seed) {
        Collections.shuffle(coll, new Random(seed));
    }

    public static void shuffleCollection(List<?> coll) {
        Collections.shuffle(coll);
    }
}
