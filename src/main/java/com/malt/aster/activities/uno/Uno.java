package com.malt.aster.activities.uno;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.ActivityPhase;
import com.malt.aster.activities.ActivityType;
import com.malt.aster.activities.uno.cards.ActionUnoCard;
import com.malt.aster.activities.uno.cards.CardAction;
import com.malt.aster.activities.uno.cards.UnoCard;
import com.malt.aster.activities.uno.cards.ValuedUnoCard;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Uno implements Activity {

    private final User commander;

    Message unoMessage;

    GuildMessageReceivedEvent originalEvent;

    private final List<User> participants;

    private boolean started;

    private ActivityPhase currentActivityPhase;

    private final Queue<ActivityPhase> phases;

    private static final List<Color> colors = Arrays.asList(Color.RED, Color.GREEN, Color.YELLOW, Color.GREEN);

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
        participants = new ArrayList<>();
        phases = new LinkedBlockingQueue<>();
        originalEvent = event;
    }

    @Override
    public void onStart() {
        if(!started) {
            unoMessage = originalEvent.getMessage();

            // Add all the phases for this activity
            addPhase(new UnoStartPhase(this));
            addPhase(new UnoMainGame(this));

            update();

            started = true;
        }
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        currentActivityPhase.handleMessage(evt);
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {
        currentActivityPhase.handleReaction(evt);
    }

    @Override
    public User getCommander() {
        return commander;
    }

    @Override
    public ActivityType getType() {
        return ActivityType.UNO;
    }

    private void addPhase(ActivityPhase phase) {
        phases.add(phase);
    }

    public void update() {
        if(phases.isEmpty())
            cleanUp();
        else {
            currentActivityPhase = phases.poll();
            currentActivityPhase.onStart();
        }
    }

    /**
     * Returns the list of participants for the game
     * @return A list of {@link User}s that are participating in the game
     */
    List<User> getParticipants() {
        return participants;
    }

    @Override
    public void cleanUp() {
        System.out.println("Uno@cleanUp: Cleaning up...");
        // TODO implement once game logic is complete
    }

    /**
     * Populates a deck of {@link UnoCard}s. Used to load any collection of cards with cards.
     * @param cards The collection of cards to load
     */
    static void obtainCards(Collection<? super UnoCard> cards) {
        for(Color color : colors) {
            cards.add(new ValuedUnoCard(0, color));

            for (int i = 1; i < 10; i++) {
                cards.add(new ValuedUnoCard(i, color));
                cards.add(new ValuedUnoCard(i, color));
            }

            for (int i = 0; i < 2; i++) {
                cards.add(new ActionUnoCard(CardAction.DRAW_TWO));
                cards.add(new ActionUnoCard(CardAction.SKIP));
                cards.add(new ActionUnoCard(CardAction.REVERSE));
            }
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new ActionUnoCard(CardAction.WILD));
            cards.add(new ActionUnoCard(CardAction.WILD_DRAW_FOUR));
        }
    }


    /**
     * @return A list of cards from scratch. Specifically returns an ArrayList.
     */
    static List<UnoCard> obtainCards() {
        List<UnoCard> cards = new ArrayList<>();
        obtainCards(cards);
        return cards;
    }
}
