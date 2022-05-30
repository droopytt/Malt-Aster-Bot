package com.malt.aster;

import com.malt.aster.core.Bot;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        if (args.length == 0) {
            throw new IllegalArgumentException("Please enter a bot token");
        }
        Bot.init(args[0]);
    }
}
