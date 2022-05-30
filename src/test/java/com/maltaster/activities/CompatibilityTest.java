package com.maltaster.activities;

import com.malt.aster.activities.uno.UnoMainGame;
import com.malt.aster.activities.uno.cards.UnoSuit;
import com.malt.aster.activities.uno.cards.ValuedUnoCard;
import org.junit.Assert;
import org.junit.Test;

public class CompatibilityTest {

    @Test
    public void testValueEquality() {
        ValuedUnoCard card1 = new ValuedUnoCard(4, UnoSuit.BLUE);
        ValuedUnoCard card2 = new ValuedUnoCard(4, UnoSuit.RED);

        Assert.assertTrue(UnoMainGame.checkIfSameValue(card1, card2));
    }
}
