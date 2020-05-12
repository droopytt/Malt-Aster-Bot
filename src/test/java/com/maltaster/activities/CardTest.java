package com.maltaster.activities;

import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.uno.Uno;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Stack;

public class CardTest {

    @Test
    public void correctCardAmountTest() {
        Stack<Card> cards = new Stack<>();
        Uno.obtainCards(cards);
        assert cards.size() == 108;
    }

    @Test
    public void createCardsFromScratch() {
        List<Card> cards = Uno.obtainCards();
        assert cards.size() == 108;
    }
}
