package com.malt.aster.activities.uno;

import com.malt.aster.activities.uno.cards.UnoCard;
import net.dv8tion.jda.api.entities.User;

import java.util.Collections;
import java.util.List;

/**
 * A helper class to map each {@link User} in a {@link Uno} game to their corresponding {@link UnoCard} list and their score for the game so far
 */
class UnoGameData {

    private final User user;

    private List<UnoCard> cards;
    private int score;

    public UnoGameData(User user, List<UnoCard> cards) {
        this.user = user;
        this.cards = cards;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int amount) {
        score += amount;
    }

    /**
     * Returns a <b>view</b> of this user's cards.
     * @return A view of the cards of this user
     */
    public List<UnoCard> getCards() {
        return Collections.unmodifiableList(cards);
    }

    public void addCard(UnoCard card) {
        cards.add(card);
    }

    public void removeCard(UnoCard card) {
        cards.remove(card);
    }

    public void setCards(List<UnoCard> cards) {
        this.cards = cards;
    }

    public User getUser() {
        return user;
    }
}
