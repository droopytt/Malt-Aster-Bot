package com.malt.aster.activities.uno;

public class ActionUnoCard extends UnoCard {

    private final CardAction action;

    public ActionUnoCard(CardAction action) {
        super();
        this.action = action;
    }

    public CardAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ActionUnoCard{" +
                "action=" + action +
                '}';
    }
}
