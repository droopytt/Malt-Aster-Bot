package com.malt.aster.activities.uno;

import com.malt.aster.activities.uno.cards.UnoCard;
import net.dv8tion.jda.api.entities.PrivateChannel;
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

    private final Map<User, Collection<UnoCard>> participantCards;
    private int currentPlayerIndex;

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
        Uno.obtainCards(coloredCards);
        Collections.shuffle(coloredCards);

        System.out.println("UnoMainGame@onStart: Card size: " + coloredCards.size());

        int cardsPerPerson = coloredCards.size()/participants.size();

        // Add the cards to each participant
        participants.forEach(participant -> {
            List<UnoCard> playerCards = new ArrayList<>();
            participantCards.put(participant, playerCards);
            for (int i = 0; i < cardsPerPerson && !coloredCards.isEmpty(); i++)
                playerCards.add(coloredCards.pop());
        });

        // Random person starts first
        Collections.shuffle(participants);

        System.out.println("UnoMainGame@onStart: " + participantCards);

        notifyAllUserCards();
        turnMessage();
    }

    /**
     * Prints the message to all participants as to who is currently having their turn right now
     */
    private void turnMessage() {
        participants.forEach(participant -> participant.openPrivateChannel()
                .queue(channel -> {
                    String currentUserNickname = Objects.requireNonNull(uno.getGuild().getMember(getCurrentPlayer())).getEffectiveName();
                    StringBuilder stringBuilder = new StringBuilder("It is currently **");

                    // Message changes depending on who the message is being sent to: the participants or the player who has their turn right now.
                    if(participant.equals(getCurrentPlayer()))
                        stringBuilder.append("your");
                    else
                        stringBuilder.append(currentUserNickname).append("'s");
                    stringBuilder.append("** turn");

                    if(participant.equals(getCurrentPlayer()))
                        stringBuilder.append("\n").append("Please choose a card");

                    channel.sendMessage(stringBuilder.toString().trim()).queue();
                }));
    }

    /**
     * Notifies all users of their current cards
     */
    private void notifyAllUserCards() {
        participants.forEach(participant -> participant.openPrivateChannel().queue(callbackChannel -> notifyCards(participant, callbackChannel)));
    }

    /**
     * Print the participant cards to the participant in direct messages
     * @param participant The participant for which the cards are to be printed
     * @param channel The message channel (private) to print the cards to
     */
    private void notifyCards(User participant, PrivateChannel channel) {
        StringBuilder stringBuilder = new StringBuilder();

        Collection<UnoCard> cards = participantCards.get(participant);
        stringBuilder.append("You have ").append(cards.size()).append(" cards: \n");

        cards.forEach(card -> stringBuilder.append(card).append("\n"));
        channel.sendMessage(stringBuilder.toString()).queue();
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {
        // TODO Decide turn logic and card values
    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {

    }

    /**
     * Gets the player whose turn it currently is
     * @return User The user who is currently taking their turn
     */
    private User getCurrentPlayer() {
        return participants.get(currentPlayerIndex);
    }

    /**
     * Starts the next turn
     */
    private void nextTurn() {
        currentPlayerIndex++;
    }
}
