package com.malt.aster.activities.uno;

import com.malt.aster.activities.Activity;
import com.malt.aster.activities.ActivityPhase;
import com.malt.aster.activities.ActivityType;
import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.uno.cards.*;
import com.malt.aster.core.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Uno implements Activity {

    private final User commander;

    private final GuildMessageReceivedEvent originalEvent;

    final List<User> participants;

    private boolean started;

    private ActivityPhase currentActivityPhase;

    private final Queue<ActivityPhase> phases;

    private static final List<UnoSuit> suits = Arrays.asList(UnoSuit.RED, UnoSuit.GREEN, UnoSuit.YELLOW, UnoSuit.BLUE);

    public Uno(GuildMessageReceivedEvent event) {
        this.commander = event.getAuthor();
        participants = new ArrayList<>();
        phases = new LinkedBlockingQueue<>();
        originalEvent = event;
    }

    // Returns a random suit that isn't a wild suit
    public static UnoSuit getRandomColouredSuit() {
        return suits.get(new Random().nextInt(suits.size()));
    }

    @Override
    public void onStart() {
        if(!started) {
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
    public void handlePrivateMessage(PrivateMessageReceivedEvent evt) {
        currentActivityPhase.handlePrivateMessage(evt);
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
     * Populates a deck of {@link UnoCard}s. Used to load any collection of cards with cards.
     *
     * @param cards The collection of cards to load
     */
    public static void obtainCards(Collection<? super UnoCard> cards) {
        for (UnoSuit suit : suits) {
            cards.add(new ValuedUnoCard(0, suit));

            for (int i = 1; i < 10; i++) {
                cards.add(new ValuedUnoCard(i, suit));
                cards.add(new ValuedUnoCard(i, suit));
            }

            for (int i = 0; i < 2; i++) {
                cards.add(new ActionUnoCard(CardAction.DRAW_TWO, suit));
                cards.add(new ActionUnoCard(CardAction.SKIP, suit));
                cards.add(new ActionUnoCard(CardAction.REVERSE, suit));
            }
        }

        for (int i = 0; i < 4; i++) {
            cards.add(new ActionUnoCard(CardAction.WILD, UnoSuit.WILD));
             cards.add(new ActionUnoCard(CardAction.WILD_DRAW_FOUR, UnoSuit.WILD));
        }
    }

    /**
     * @return A list of cards from scratch.
     */
    public static List<Card> obtainCards() {
        List<Card> cards = new ArrayList<>();
        obtainCards(cards);
        return cards;
    }

    @Override
    public void cleanUp() {
        System.out.println("Uno@cleanUp: Cleaning up...");
        Bot.getInstance().getActivityManager().getActivityManagerForGuild(originalEvent.getGuild()).removeActivity(this);
    }

    /**
     * Returns the list of participants for the game
     *
     * @return A list of {@link User}s that are participating in the game
     */
    public Set<User> getParticipants() {
        return Collections.unmodifiableSet(new HashSet<>(participants));
    }

    public GuildMessageReceivedEvent getOriginalEvent() {
        return originalEvent;
    }

    public Guild getGuild() {
        return originalEvent.getGuild();
    }
}
