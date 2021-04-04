package com.malt.aster.utils;

import com.malt.aster.activities.uno.cards.UnoCard;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class Utils {

    public static String getEffectiveName(User user, Guild guild) {
        return Objects.requireNonNull(guild.getMember(user), "No member found for this user in that guild").getEffectiveName();
    }

    public static void shuffleCollection(List<?> coll, int seed) {
        Collections.shuffle(coll,new Random(seed));
    }

    public static void shuffleCollection(List<?> coll) {
        Collections.shuffle(coll);
    }
}
