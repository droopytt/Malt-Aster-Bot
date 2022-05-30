package com.malt.aster.activities.uno;

import com.malt.aster.activities.uno.cards.*;
import com.malt.aster.utils.Constants;
import com.malt.aster.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.malt.aster.activities.uno.Uno.obtainCards;
import static com.malt.aster.utils.Utils.shuffleCollection;
import static java.util.Collections.shuffle;
import static java.util.Objects.requireNonNull;

/**
 * Decide the main logic of the game
 * <p>
 * You have access to the participants, the message that initiated this activity, and of course, the
 * root uno activity this phase is a part of.
 */
public class UnoMainGame extends UnoPhase {

    public static final int INITIAL_CARDS_PER_PLAYER = 7;
    private final Map<User, UnoGameData> participantData;
    private final Stack<UnoCard> discardPile;
    private final Stack<UnoCard> drawPile;

    // Are we waiting on a user to decide the colour of a wildcard?
    private boolean waitingOnWildChoice;

    private final Map<User, UnoCard> recentlyPenalised;

    private final Comparator<User> scoreComparator;

    private int erroneousMessagesRemaining;
    private int currentPlayerIndex;
    private int turnNumber;

    private boolean reversed;

    public UnoMainGame(Uno uno) {
        super(uno);
        participantData = new HashMap<>();
        discardPile = new Stack<>();
        drawPile = new Stack<>();
        erroneousMessagesRemaining = Constants.UNO_MAX_ERRONEOUS_MESSAGES;

        recentlyPenalised = new HashMap<>();

        scoreComparator = (o1, o2) ->
                participantData.get(o2).getScore() - participantData.get(o1).getScore();
    }

    /**
     * Checks if the two cards are equal in value
     *
     * @param first  The first card
     * @param second The second card
     * @return true if both cards are of the same value, false otherwise
     */
    public static boolean checkIfSameValue(UnoCard first, UnoCard second) {
        // Can't compare their values if either one of them are not valued uno cards
        if (!first.isValued() || !second.isValued()) return false;

        ValuedUnoCard firstValuedCard = (ValuedUnoCard) first;
        ValuedUnoCard otherValuedCard = (ValuedUnoCard) second;

        return firstValuedCard.getValue() == otherValuedCard.getValue();
    }

    private static boolean checkIfWild(UnoCard card) {
        if (!card.isAction()) return false;

        ActionUnoCard actionUnoCard = (ActionUnoCard) card;

        return actionUnoCard.isWild();
    }

    public static boolean canMatch(UnoCard currentCard, UnoCard discardCard) {
        return checkIfSameValue(currentCard, discardCard)
                || currentCard.getSuit() == discardCard.getSuit()
                || checkIfWild(currentCard);
    }

    /**
     * Load the cards into the participant cards mappings
     */
    @Override
    public void onStart() {
        Stack<UnoCard> freshDeck = new Stack<>();
        obtainCards(freshDeck);
        shuffleCollection(freshDeck);

        System.out.println("UnoMainGame@onStart: Card size: " + freshDeck.size());

        // Add the cards to each participant

        for (User participant : participants) {
            List<UnoCard> playerCards = new ArrayList<>();

            if (participantData.get(participant) == null)
                participantData.put(participant, new UnoGameData(participant, playerCards));
            else participantData.get(participant).setCards(playerCards);

            for (int i = 0; i < INITIAL_CARDS_PER_PLAYER; i++) {
                playerCards.add(freshDeck.pop());
            }
        }

        discardPile.add(freshDeck.pop());

        // Add all remaining cards to the draw pile
        drawPile.addAll(freshDeck);

        // Random person starts first
        shuffle(participants);

        System.out.println("UnoMainGame@onStart: " + participantData);
        System.out.println("UnoMainGame@onStart: Fresh deck: " + freshDeck);

        nextTurn();
    }

    /**
     * Notifies all users of their current cards
     */
    private void notifyAllUserCards() {
        participants.forEach(participant ->
                participant.openPrivateChannel().queue(callbackChannel -> notifyCards(participant, callbackChannel)));
    }

    /**
     * Print the participant cards to the participant in direct messages
     *
     * @param participant The participant for which the cards are to be printed
     * @param channel     The message channel (private) to print the cards to
     */
    private void notifyCards(User participant, PrivateChannel channel) {
        StringBuilder stringBuilder = new StringBuilder();

        List<UnoCard> cards = participantData.get(participant).getCards();
        stringBuilder
                .append("__Turn ")
                .append(turnNumber)
                .append("__\nYou have ")
                .append(cards.size())
                .append(" cards: \n");

        for (int i = 0; i < cards.size(); i++) {
            UnoCard card = cards.get(i);
            stringBuilder.append("`").append(i + 1).append(".` ");
            if (canMatch(card, discardPile.peek()) && participant.equals(getCurrentPlayer()))
                stringBuilder.append("**").append(card).append("**");
            else stringBuilder.append(card);

            stringBuilder.append("\n");
        }

        channel.sendMessage(stringBuilder.toString()).queue();
    }

    /**
     * Prints the message to all participants as to who is currently having their turn right now
     */
    private void turnMessage() {
        for (User participant : participants) {
            participant.openPrivateChannel().queue(channel -> {
                String currentUserNickname = requireNonNull(uno.getGuild().getMember(getCurrentPlayer()))
                        .getEffectiveName();

                StringBuilder stringBuilder = new StringBuilder("It is now **");

                // Message changes depending on who the message is being sent to:
                // the participants or the player who has their turn right now.
                if (participant.equals(getCurrentPlayer())) stringBuilder.append("your");
                else stringBuilder.append(currentUserNickname).append("'s");
                stringBuilder.append("** turn!");

                if (participant.equals(getCurrentPlayer()))
                    stringBuilder
                            .append("\n")
                            .append("Please choose a card option as numbered ")
                            .append(participant.getAsMention());
                else stringBuilder.append(". Please wait for them to complete their turn.");

                stringBuilder.append("\n\n");

                if (discardPile.isEmpty()) stringBuilder.append("There was no previous card.");
                else stringBuilder.append("The last card was ").append(discardPile.peek());

                channel.sendMessage(stringBuilder.toString().trim()).queue();
            });
        }
    }

    @Override
    public void handleMessage(GuildMessageReceivedEvent evt) {}

    @Override
    public void handleReaction(GuildMessageReactionAddEvent evt) {}

    @Override
    public void handlePrivateMessage(PrivateMessageReceivedEvent evt) {
        User sender = evt.getAuthor();
        PrivateChannel channel = evt.getChannel();
        String messageContent = evt.getMessage().getContentRaw().toLowerCase();

        if (messageContent.startsWith(Constants.ACTIVITY_MESSAGE_PREFIX)) {
            handleCommunicationMessage(evt);
        } else if (waitingOnWildChoice) {
            UnoSuit desiredSuit = null;
            try {
                desiredSuit = UnoSuit.valueOf(messageContent.toUpperCase());
            } catch (IllegalArgumentException e) {
                handleErroneousOption(channel, messageContent + " is not a valid suit.");
            }
            if (desiredSuit == UnoSuit.WILD)
                handleErroneousOption(channel, "Please enter a valid suit to set the wildcard to");
            else {
                ActionUnoCard actionUnoCard = (ActionUnoCard) discardPile.peek();
                boolean drawFour = actionUnoCard.getAction() == CardAction.WILD_DRAW_FOUR;

                actionUnoCard.setSuit(desiredSuit);

                StringBuilder stringBuilder = new StringBuilder();

                assert desiredSuit != null;
                stringBuilder
                        .append(Utils.getEffectiveName(sender, uno.getGuild()))
                        .append(" has chosen the wild card colour to be ")
                        .append(desiredSuit.toString().toLowerCase());

                if (drawFour) {
                    stringBuilder
                            .append("\n")
                            .append("**")
                            .append(Utils.getEffectiveName(sender, uno.getGuild()))
                            .append("**")
                            .append(" has also forced ")
                            .append("**")
                            .append(Utils.getEffectiveName(getNextPlayer(), uno.getGuild()))
                            .append("**")
                            .append(" to draw four cards.");
                    draw(getNextPlayer(), 4);
                }

                participants.forEach(user -> user.openPrivateChannel().queue(privateChannel -> privateChannel
                        .sendMessage(stringBuilder.toString())
                        .queue()));
                waitingOnWildChoice = false;
                nextTurn();
            }
        } else if (recentlyPenalised.get(sender) != null) {
            // If they have yet to decide if they play their card after being penalised
            switch (messageContent.toLowerCase()) {
                case "skip":
                    uno.participants.forEach(user -> user.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage(
                                    Utils.getEffectiveName(sender, uno.getGuild()) + " has chosen " + messageContent)
                            .queue()));
                    recentlyPenalised.remove(sender);
                    nextTurn();
                    break;
                case "play":
                    uno.participants.forEach(user -> user.openPrivateChannel().queue(privateChannel -> privateChannel
                            .sendMessage(
                                    Utils.getEffectiveName(sender, uno.getGuild()) + " has chosen " + messageContent)
                            .queue()));
                    UnoCard penaltyCard = recentlyPenalised.remove(sender);
                    doTurnRoutine(sender, penaltyCard);
                    break;
                default:
                    handleErroneousOption(evt.getChannel(), "Please pick either **play** or **skip**");
            }

        } else if (sender.equals(getCurrentPlayer())) {
            try {
                int cardIndex = Integer.parseInt(messageContent) - 1;

                if (validChoice(sender, cardIndex)) {
                    // Get the cards of the current player and then check the card at the top of the pile
                    List<UnoCard> currentPlayerCards =
                            participantData.get(sender).getCards();
                    UnoCard chosenCard = currentPlayerCards.get(cardIndex);
                    UnoCard topCard = discardPile.peek();

                    if (!canMatch(chosenCard, topCard)) {
                        handleErroneousOption(channel, "Please pick a valid matching card");
                        return;
                    }

                    // If all is well
                    doTurnRoutine(sender, chosenCard);

                } else {
                    handleErroneousOption(
                            channel, "That's not a valid card position. Pick one marked with the numbers");
                }
            } catch (NumberFormatException e) {
                handleErroneousOption(channel, "Select an option from its number. Try again.");
            }
        }
    }

    /**
     * This method is executed given that the player has picked a valid option
     *
     * @param chosenCard The card to select
     * @param sender     The player that is choosing the card
     */
    public void doTurnRoutine(User sender, UnoCard chosenCard) {
        List<UnoCard> currentPlayerCards = participantData.get(sender).getCards();
        discardPile.add(chosenCard);
        participantData.get(sender).removeCard(chosenCard);

        if (currentPlayerCards.size() == 0) {
            handleVictory(getCurrentPlayer());
            return;
        }

        if (currentPlayerCards.size() == 1) notifyCurrentPlayerHasOneCardLeft(sender);

        if (chosenCard.isAction()) handleActionCard(sender, chosenCard);
        else nextTurn();
    }

    /**
     * Decide logic for handling action cards here
     *
     * @param sender     The player who has chosen the action card
     * @param chosenCard The card that lead to the action (to be converted to an action card to infer the action)
     */
    private void handleActionCard(User sender, UnoCard chosenCard) {
        CardAction cardAction = ((ActionUnoCard) chosenCard).getAction();
        cardAction.perform(sender, this);
    }

    /**
     * Messages all the players that the player has one card left (so as to shout "Uno" in person)
     *
     * @param user The player who has one card left
     */
    private void notifyCurrentPlayerHasOneCardLeft(User user) {
        // TODO If the player will have one card left after the turn, THEY should say it. IF they don't the other
        // players have the opportunity to call them out - if they are called out they will be forced to draw 2
        uno.participants.forEach(player -> player.openPrivateChannel().queue(privateChannel -> {
            String playerWithOneCardLeftName =
                    requireNonNull(uno.getGuild().getMember(user)).getEffectiveName();
            privateChannel
                    .sendMessage("**" + playerWithOneCardLeftName + " has one card left!**")
                    .queue();
        }));
    }

    private void handleErroneousOption(PrivateChannel channel, String message) {
        if (erroneousMessagesRemaining == 0) {
            StringBuilder stringBuilder = new StringBuilder(
                    "You couldn't pick a correct option so your turn was skipped and you were forced to "
                            + "draw a card.");

            // If they were supposed to decide the wild card
            if (waitingOnWildChoice) {
                UnoSuit suit = Uno.getRandomColouredSuit();
                StringBuilder stringBuilderForAll = new StringBuilder();
                stringBuilderForAll
                        .append("\n\n")
                        .append("The wildcard suit was set to ")
                        .append(suit.toString().toLowerCase());
                stringBuilder.append(stringBuilderForAll);
                ((ActionUnoCard) discardPile.peek()).setSuit(suit);
                waitingOnWildChoice = false;
                participants.stream()
                        .filter(participant -> !participant.equals(getCurrentPlayer()))
                        .forEach(participant -> participant.openPrivateChannel().queue(privateChannel -> privateChannel
                                .sendMessage(stringBuilderForAll)
                                .queue()));
            }

            // Add the card from the draw pile
            participantData.get(getCurrentPlayer()).addCard(drawCard());
            channel.sendMessage(stringBuilder.toString()).queue();
            nextTurn();
        } else {
            channel.sendMessage(message + " (" + erroneousMessagesRemaining + "/" + Constants.UNO_MAX_ERRONEOUS_MESSAGES
                            + " chances remaining)")
                    .queue();
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
        List<UnoCard> cards = participantData.get(user).getCards();
        return cardIndex < cards.size() && cardIndex >= 0;
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
    public void nextTurn() {
        updatePlayerIndex();
        turnNumber++;
        erroneousMessagesRemaining = Constants.UNO_MAX_ERRONEOUS_MESSAGES;
        notifyAllUserCards();

        User currentPlayer = getCurrentPlayer();
        List<UnoCard> currentPlayerCards = participantData.get(currentPlayer).getCards();

        // If there aren't any cards the player can possibly use, consume a card from the draw pile and if it works
        // then apply that, otherwise force them to keep the card
        if (currentPlayerCards.stream()
                .noneMatch(card -> (card.getSuit().equals(discardPile.peek().getSuit())
                                || card.getSuit().equals(UnoSuit.WILD))
                        || checkIfSameValue(card, discardPile.peek()))) {
            penalisePlayer(currentPlayer);
        } else {
            turnMessage();
        }
    }

    /**
     * Increments the current player index, so that the turn updates. Used when skipping turns as well, and does
     * this in accordance to whether the board is reversed or not.
     */
    public void updatePlayerIndex() {
        currentPlayerIndex = getNextPlayerIndex();
    }

    private int getNextPlayerIndex() {
        int idx = reversed
                ? (currentPlayerIndex - 1) % participants.size()
                : (currentPlayerIndex + 1) % participants.size();
        if (idx < 0) idx = participants.size() - 1;
        return idx;
    }

    /**
     * Returns the player that is to go next after the current one
     * Does not update the current player
     *
     * @return The next player
     */
    public User getNextPlayer() {
        return participants.get(getNextPlayerIndex());
    }

    /**
     * Sends a message to all users in the activity
     *
     * @param evt The private message event to handle
     */
    private void handleCommunicationMessage(PrivateMessageReceivedEvent evt) {

        // If they typed an exclamation mark but didnt follow it with a communication message, then don't do anything
        if (evt.getMessage().getContentRaw().split(" ").length <= 1) return;

        // Refer to them by the name in the server the activity was started in
        String userName = requireNonNull(uno.getGuild().getMember(evt.getAuthor()))
                .getEffectiveName();

        // Takes remainder of message, for example, if message is sent through !say hi,
        // actualMessage now refers to "hi"
        String actualMessage = evt.getMessage().getContentRaw().split(" ", 2)[1];

        participants.stream().filter(user -> !user.equals(evt.getAuthor())).forEach(user -> user.openPrivateChannel()
                .queue(channel -> channel.sendMessage("**" + userName + "** says: " + actualMessage)
                        .queue()));
    }

    private void penalisePlayer(User player) {
        // The card to add
        UnoCard penaltyCard = drawCard();

        participantData.get(getCurrentPlayer()).addCard(penaltyCard);
        sendPenaltyMessageToAll(player, penaltyCard);
    }

    /**
     * Notify all the players that the player has been penalised because they did not have a card that they could use
     *
     * @param penalisedPlayer The player that has been penalised
     * @param penaltyCard     The penaltyCard that was added as a result of the penalty
     */
    private void sendPenaltyMessageToAll(User penalisedPlayer, UnoCard penaltyCard) {
        String userEffectiveName = Utils.getEffectiveName(penalisedPlayer, uno.getGuild());

        if (!canMatch(penaltyCard, discardPile.peek())) {
            if (penaltyCard.isAction()) {
                ActionUnoCard actionUnoCard = (ActionUnoCard) penaltyCard;
                if (actionUnoCard.isWild()) System.out.println("WTF IT SHOULD BE MATCHING??");
            }
            // Logic for deciding if we skip the user if they cant play the penalty card anyway
            uno.participants.forEach(user -> user.openPrivateChannel().queue(channel -> channel.sendMessage(
                            userEffectiveName
                                    + " has been penalised and forced to draw an extra card as they did not have any to match. This card also did not match so their turn was skipped entirely!")
                    .queue()));
            nextTurn();
        } else {
            StringBuilder sb = new StringBuilder(
                    userEffectiveName + " has been penalised - they were given a card from the draw pile"
                            + ". They can decide " + "if they wish to play it or skip.");
            uno.participants.stream()
                    .filter(user -> !user.equals(getCurrentPlayer()))
                    .forEach(user -> user.openPrivateChannel()
                            .queue(channel -> channel.sendMessage(sb.toString()).queue()));

            sb.append("\n\n")
                    .append(penalisedPlayer.getAsMention())
                    .append(" You were given a **")
                    .append(penaltyCard)
                    .append("**. \nPlease type **skip** or **play** to reflect your decision. \n")
                    .append("The most recent card was **")
                    .append(discardPile.peek())
                    .append("**.");
            penalisedPlayer.openPrivateChannel().queue(channel -> channel.sendMessage(sb.toString())
                    .queue());
            recentlyPenalised.put(penalisedPlayer, penaltyCard);
        }
    }

    /**
     * Decide what happens when a round ends (a player reaches 0 cards)
     *
     * @param winner The user that has won
     */
    private void handleVictory(User winner) {
        int scoreToAdd = 0;

        for (User participant : participants) {
            if (participant.equals(winner)) continue;

            for (UnoCard card : participantData.get(participant).getCards()) scoreToAdd += card.getScoreValue();
        }

        // Update the winner score and reset the game
        participantData.get(winner).addScore(scoreToAdd);

        StringBuilder sb = new StringBuilder();

        sb.append(Utils.getEffectiveName(winner, uno.getGuild()))
                .append(" has won ")
                .append(scoreToAdd)
                .append(" points. ")
                .append("\nThe current leaderboard is now:\n");

        AtomicInteger leaderboardIndex = new AtomicInteger(1);

        // Build the leaderboard
        participants.stream().sorted(scoreComparator).forEach(participant -> sb.append("`")
                .append(leaderboardIndex.getAndIncrement())
                .append("`")
                .append(": ")
                .append(Utils.getEffectiveName(participant, uno.getGuild()))
                .append(" - ")
                .append(participantData.get(participant).getScore())
                .append(" points\n"));

        User userWithHighestScore = participants.stream().min(scoreComparator).orElse(winner);

        // Called if game concludes entirely (25 multiplied by the participant size)
        if (participantData.get(userWithHighestScore).getScore() >= participants.size() * 25) {
            uno.cleanUp();
            // TODO add logic for currency system
            sb.append(
                            "\nThat concludes this game of UNO! You have all been rewarded for your efforts. Congratulations to ")
                    .append(userWithHighestScore)
                    .append(" on their victory!");
            participants.forEach(participant -> participant.openPrivateChannel().queue(privateChannel -> privateChannel
                    .sendMessage(sb.toString().trim())
                    .queue()));
            return;
        }

        participants.forEach(participant -> participant.openPrivateChannel().queue(privateChannel -> privateChannel
                .sendMessage(sb.toString().trim())
                .queue()));
        // Restart the game again and clear the two piles
        drawPile.clear();
        discardPile.clear();
        turnNumber = 0;
        onStart();
    }

    public void setWaitingOnWild() {
        this.waitingOnWildChoice = true;
    }

    /**
     * Draws the amount of cards from the draw pile and puts it into the provided player's cards
     *
     * @param participant The participant to add cards to
     * @param amount      The amount of cards to draw
     */
    public void draw(User participant, int amount) {
        for (int i = 0; i < amount; i++) participantData.get(participant).addCard(drawCard());
    }

    /**
     * Inverts the direction of the turns
     */
    public void reverse() {
        reversed = !reversed;
    }

    /**
     * Returns a view of the participants
     *
     * @return A view of the participants of this uno game
     */
    public List<User> getParticipants() {
        return Collections.unmodifiableList(participants);
    }

    /**
     * Return the guild this uno main game is part of
     *
     * @return The uno guild this is part of
     */
    public Guild getGuild() {
        return uno.getGuild();
    }

    private UnoCard drawCard() {
        // If we find that the draw pile is empty, then reset it and put everything from the discard pile back in
        if (drawPile.isEmpty()) {
            UnoCard topCard = discardPile.pop();

            // Temporary card list for shuffling
            List<UnoCard> discardPileAsList = new ArrayList<>(discardPile);
            shuffleCollection(discardPileAsList);
            drawPile.addAll(discardPileAsList);

            discardPile.add(topCard);
        }
        return drawPile.pop();
    }
}
