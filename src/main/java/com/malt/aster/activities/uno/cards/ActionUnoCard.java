package com.malt.aster.activities.uno.cards;

public class ActionUnoCard extends UnoCard {

    private final CardAction action;

    public ActionUnoCard(CardAction action) {
        super(UnoSuit.BLUE); // TODO change - action cards can have colours too. Inheritance hierarchy should be reworked
        this.action = action;
    }

    public CardAction getAction() {
        return action;
    }

    @Override
    public String toString() {
        String[] tokens = action.toString().split("_");

        StringBuilder stringBuilder = new StringBuilder();

        for (String token : tokens)
            stringBuilder.append(token.charAt(0)).append(token.substring(1).toLowerCase()).append(" ");

        return stringBuilder.toString().trim();
    }
}
