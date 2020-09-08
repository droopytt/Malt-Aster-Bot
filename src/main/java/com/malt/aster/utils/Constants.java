package com.malt.aster.utils;

/**
 * Constants to be used by the bot.
 * Should all be public static final
 */
public class Constants {

    public static final String CHECK_EMOTE = "\u2705";
    public static final String CROSS_EMOTE = "\u274E";

    public static final int MINIMUM_UNO_PLAYERS = 2;

    public static final int MESSAGE_LENGTH_LIMIT = 2000;

    public static final int UNO_MAX_SCORE = 500;

    public static final int UNO_MAX_ERRONEOUS_MESSAGES = 3;

    public static final String ACTIVITY_MESSAGE_PREFIX = "!";

    // Make non-instantiable
    private Constants() {
    }
}
