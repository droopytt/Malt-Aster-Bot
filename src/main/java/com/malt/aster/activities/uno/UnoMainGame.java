package com.malt.aster.activities.uno;

import com.malt.aster.activities.Card;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.*;

/**
 * Decide the main logic of the game
 *
 * You have access to the participants, the message that initiated this activity, and of course, the
 * root uno activity this phase is a part of.
 */
public class UnoMainGame extends UnoPhase {

    private final Map<User, Collection<Card>> participantCards;

    public UnoMainGame(Uno uno) {
        super(uno);
        participantCards = new HashMap<>();
    }

    /**
     * Load the cards into the participant cards mappings
     */
    @Override
    public void onStart() {
        Stack<UnoCard> coloredCards = new Stack<>();
        Collections.shuffle(coloredCards);
        Uno.obtainCards(coloredCards);

        System.out.println("UnoMainGame@onStart: Card size: " + coloredCards.size());

        int cardsPerPerson = coloredCards.size()/participants.size();

        // Add the cards to each participant
        participants.forEach(x -> {
            List<Card> playerCards = new ArrayList<>();
            participantCards.put(x, playerCards);
            for (int i = 0; i < cardsPerPerson && !coloredCards.isEmpty(); i++)
                playerCards.add(coloredCards.pop());
        });

        System.out.println("UnoMainGame@onStart: " + participantCards);
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        // TODO Decide turn logic and card values
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {

    }
}
