package com.malt.aster.activities.uno.cards.actions;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.activities.uno.cards.CardAction;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to abstract the interaction from the rest of the code between a {@link CardAction} enumeration field and the corresponding action
 * by mapping between the two
 */
public class UnoActionHandler {

    private static UnoActionHandler instance;

    private Map<CardAction, UnoActionable> actionMap;

    private UnoActionHandler() {
        actionMap = new HashMap<>();
        actionMap.put(CardAction.SKIP, new SkipAction());
        actionMap.put(CardAction.DRAW_TWO, new DrawTwoAction());
        actionMap.put(CardAction.REVERSE, new ReverseAction());
        actionMap.put(CardAction.WILD, new WildCardAction());
        actionMap.put(CardAction.WILD_DRAW_FOUR, new WildDrawFourAction());
    }

    public static UnoActionHandler getInstance() {
        if (instance == null) instance = new UnoActionHandler();
        return instance;
    }

    public void handle(User sender, CardAction action, UnoMainGame mainGame) {
        actionMap.get(action).perform(sender, mainGame);
    }
}
