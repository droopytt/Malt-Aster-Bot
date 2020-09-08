package com.malt.aster.activities.uno.cards;

public class ActionUnoCard extends UnoCard {

    private final CardAction action;

    public ActionUnoCard(CardAction action, UnoSuit suit) {
        super(suit);
        this.action = action;
    }

    public CardAction getAction() {
        return action;
    }

    public boolean isWild() {
        return action == CardAction.WILD || action == CardAction.WILD_DRAW_FOUR;
    }

    @Override
    public int getScoreValue() {
        return action.getScoreValue();
    }

    @Override
    public String toString() {
        String[] tokens = action.toString().split("_");

        StringBuilder stringBuilder = new StringBuilder();

        for (String token : tokens)
            stringBuilder.append(token.charAt(0)).append(token.substring(1).toLowerCase()).append(" ");

        if(!this.isWild())
            stringBuilder.append("(").append(suit.toString().charAt(0)).append(suit.toString().substring(1).toLowerCase()).append(")");

        return stringBuilder.toString().trim();
    }
}
