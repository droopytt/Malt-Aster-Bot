package com.maltaster.activities;

import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.uno.Uno;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Stack;

import static com.malt.aster.activities.uno.Uno.obtainCards;

public class CardTest {

    public static final int TOTAL_CARDS = 108;

    @Test
    public void correctCardAmountTest() {
        Stack<Card> cards = new Stack<>();
        obtainCards(cards);
        assert cards.size() == TOTAL_CARDS;
    }

    @Test
    public void createCardsFromScratch() {
        List<Card> cards = obtainCards();
        assert cards.size() == TOTAL_CARDS;
    }
}
