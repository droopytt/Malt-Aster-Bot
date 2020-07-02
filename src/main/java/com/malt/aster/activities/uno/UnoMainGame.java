package com.malt.aster.activities.uno;

import com.malt.aster.activities.cards.Card;
import com.malt.aster.activities.uno.cards.UnoCard;
import com.malt.aster.activities.uno.cards.UnoSuit;
import com.malt.aster.activities.uno.cards.ValuedUnoCard;
import com.malt.aster.utils.Constants;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.*;

/**
 * Decide the main logic of the game
 * <p>
 * You have access to the participants, the message that initiated this activity, and of course, the
 * root uno activity this phase is a part of.
 */
public class UnoMainGame extends UnoPhase {

    private final Map<User, List<UnoCard>> participantCards;
    private final Stack<UnoCard> discardPile;
    private final Stack<UnoCard> drawPile;

    private int erroneousMessagesRemaining;
    private int currentPlayerIndex;

    private boolean reversed;

    public UnoMainGame(Uno uno) {
        super(uno);
        participantCards = new HashMap<>();
        discardPile = new Stack<>();
        drawPile = new Stack<>();
        erroneousMessagesRemaining = Constants.UNO_MAX_ERRONEOUS_MESSAGES;
    }

    /**
     * Load the cards into the participant cards mappings
     */
    @Override
    public void onStart() {
        Stack<UnoCard> freshDeck = new Stack<>();
        Uno.obtainCards(freshDeck);
        Collections.shuffle(freshDeck);

        System.out.println("UnoMainGame@onStart: Card size: " + freshDeck.size());

        // Add the cards to each participant
        participants.forEach(participant -> {
            List<UnoCard> playerCards = new ArrayList<>();
            participantCards.put(participant, playerCards);
            for (int i = 0; i < 7; i++)
                playerCards.add(freshDeck.pop());
        });

        discardPile.add(freshDeck.pop());

        // Add all remaining cards to the draw pile
        drawPile.addAll(freshDeck);

        // Random person starts first
        Collections.shuffle(participants);

        System.out.println("UnoMainGame@onStart: " + participantCards);
        System.out.println("UnoMainGame@onStart: Fresh deck: " + freshDeck);

        notifyAllUserCards();
        turnMessage();
    }

    /**
     * Notifies all users of their current cards
     */
    private void notifyAllUserCards() {
        participants.forEach(participant -> participant.openPrivateChannel().queue(callbackChannel -> notifyCards(participant, callbackChannel)));
    }

    /**
     * Print the participant cards to the participant in direct messages
     *
     * @param participant The participant for which the cards are to be printed
     * @param channel     The message channel (private) to print the cards to
     */
    private void notifyCards(User participant, PrivateChannel channel) {
        StringBuilder stringBuilder = new StringBuilder();

        List<UnoCard> cards = participantCards.get(participant);
        stringBuilder.append("You have ").append(cards.size()).append(" cards: \n");

        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            stringBuilder.append("`").append(i + 1).append(".` ").append(card).append("\n");
        }

        channel.sendMessage(stringBuilder.toString()).queue();
    }

    /**
     * Prints the message to all participants as to who is currently having their turn right now
     */
    private void turnMessage() {
        participants.forEach(participant -> participant.openPrivateChannel()
                .queue(channel -> {
                    String currentUserNickname = Objects.requireNonNull(uno.getGuild().getMember(getCurrentPlayer()))
                            .getEffectiveName();

                    StringBuilder stringBuilder = new StringBuilder("It is now **");

                    // Message changes depending on who the message is being sent to:
                    // the participants or the player who has their turn right now.
                    if (participant.equals(getCurrentPlayer()))
                        stringBuilder.append("your");
                    else
                        stringBuilder.append(currentUserNickname).append("'s");
                    stringBuilder.append("** turn!");

                    if (participant.equals(getCurrentPlayer()))
                        stringBuilder.append("\n").append("Please choose a card option as numbered ")
                                .append(participant.getAsMention());
                    else
                        stringBuilder.append(". Please wait for them to complete their turn.");

                    stringBuilder.append("\n\n");

                    if (discardPile.isEmpty())
                        stringBuilder.append("There was no previous card.");
                    else
                        stringBuilder.append("The last card was ").append(discardPile.peek());

                    channel.sendMessage(stringBuilder.toString().trim()).queue();
                }));
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {

    }

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {

    }

    @Override
    public void handlePrivateMessage(PrivateMessageReceivedEvent evt) {
        User sender = evt.getAuthor();
        PrivateChannel channel = evt.getChannel();

        if(evt.getMessage().getContentRaw().startsWith(Constants.ACTIVITY_MESSAGE_PREFIX + "say")) {
            handleCommunicationMessage(evt);
        } else {

            if (sender.equals(getCurrentPlayer())) {
                try {
                    int cardIndex = Integer.parseInt(evt.getMessage().getContentRaw()) - 1;

                    if (validChoice(sender, cardIndex)) {
                        // Get the cards of the current player and then check the card at the top of the pile
                        List<UnoCard> currentPlayerCards = participantCards.get(sender);
                        UnoCard chosenCard = currentPlayerCards.get(cardIndex);
                        UnoCard topCard = discardPile.peek();

                        // TODO decide logic what happens between conflicting card suits

                        if (!topCard.getSuit().equals(chosenCard.getSuit())) {
                            handleErroneousOption(channel, "Please pick a valid card.");
                            return;
                        }

                        if (topCard.isValued() && chosenCard.isValued()) {
                            ValuedUnoCard topValued = (ValuedUnoCard) topCard;
                            ValuedUnoCard chosenValued = (ValuedUnoCard) chosenCard;

                            if (topValued.getValue() != chosenValued.getValue()) {
                                handleErroneousOption(channel, "The card numbers are not the same - please pick a valid card.");
                                return;
                            }
                        }

                        discardPile.add(chosenCard);
                        currentPlayerCards.remove(chosenCard);


                        nextTurn();
                    } else {
                        handleErroneousOption(channel,
                                "That's not a valid card position. Pick one marked with the numbers");
                    }
                } catch (NumberFormatException e) {
                    handleErroneousOption(channel, "Select an option from its number. Try again.");
                }
            }
        }
    }

    private void handleErroneousOption(PrivateChannel channel, String message) {
        if (erroneousMessagesRemaining == 0) {
            channel.sendMessage("You couldn't pick a correct option so your turn was skipped and you were forced to " +
                    "draw a card.").queue();

            // Add the card from the draw pile
            participantCards.get(getCurrentPlayer()).add(drawPile.pop());
            nextTurn();
        } else {
            channel.sendMessage(message).queue();
            erroneousMessagesRemaining--;
        }
    }

    /**
     * Returns true if the choice (option) chosen from the messages is valid for this user
     *
     * @param cardIndex The integer representing the choice for this user. This is the raw value provided by the user.
     *                  Be aware of off-by-one errors with this.
     * @return true if the choice is valid, false otherwise (out of bounds for example is one way this can return false)
     */
    private boolean validChoice(User user, int cardIndex) {
        List<UnoCard> cards = participantCards.get(user);
        return cardIndex < cards.size() && cardIndex > 0;
    }

    /**
     * Gets the player whose turn it currently is
     *
     * @return User The user who is currently taking their turn
     */
    private User getCurrentPlayer() {
        return participants.get(currentPlayerIndex);
    }

    /**
     * Starts the next turn, notifies all users of their cards
     */
    private void nextTurn() {
        updatePlayerIndex();
        erroneousMessagesRemaining = Constants.UNO_MAX_ERRONEOUS_MESSAGES;
        notifyAllUserCards();

        List<UnoCard> nextPlayerCards = participantCards.get(getCurrentPlayer());
        // If there aren't any cards the player can possibly use, consume a card from the draw pile and if it works
        // then apply that, otherwise force them to keep the card
        if (nextPlayerCards
                .stream().noneMatch(card -> card.getSuit().equals(discardPile.peek().getSuit()) || card.getSuit().equals(UnoSuit.WILD))) {
            // The card to add
            UnoCard penaltyCard = drawPile.pop();

            // TODO decide logic for consuming the card - do we give the player the choice to use, or just draw the card?
            //nextPlayerCards.add(drawPile.pop());
        } else
            turnMessage();
    }

    private void updatePlayerIndex() {
        currentPlayerIndex = reversed ? (currentPlayerIndex - 1) % participants.size() : (currentPlayerIndex + 1) % participants.size();
    }

    /**
     * Sends a message to all users in the activity
     *
     * @param evt The private message event to handle
     */
    private void handleCommunicationMessage(PrivateMessageReceivedEvent evt) {

        // Refer to them by the name in the server the activity was started in
        String userName = uno.getGuild().getMember(evt.getAuthor()).getEffectiveName();

        // Takes remainder of message, for example, if message is sent through !say hi,
        // actualMessage now refers to "hi"
        String actualMessage = evt.getMessage().getContentRaw().split(" ", 2)[1];

        participants
                .stream()
                .filter(user -> !user.equals(evt.getAuthor()))
                .forEach(user -> user.openPrivateChannel()
                        .queue(channel -> channel.sendMessage("**" + userName + "** says: " + actualMessage).queue()));
    }
}
