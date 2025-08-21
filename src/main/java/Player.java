import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Player class represents a player in the card game and
 * manages their hand and interactions with decks
 */
public class Player implements Runnable {
    private static final int HAND_SIZE = 4;
    private final int playerId;
    private final List<Integer> hand;
    private final Deck leftDeck;
    private final Deck rightDeck;
    private final AtomicBoolean gameOver;
    private final CountDownLatch gameEndLatch;
    private final FileWriter outputWriter;
    private final Object handLock = new Object();
    private volatile boolean hasExited = false;

    /**
     * Constructor starts the player's state and
     * Write the initial hand to the output file
     * 
     * @param id           Player's unique ID.
     * @param initialHand  Initial cards dealt to the player.
     * @param leftDeck     The deck from which the player draws cards.
     * @param rightDeck    The deck to which the player discards cards.
     * @param gameOver     Shared game state flag.
     * @param gameEndLatch Latch to signal game completion.
     */
    public Player(int id, List<Integer> initialHand, Deck leftDeck, Deck rightDeck,
            AtomicBoolean gameOver, CountDownLatch gameEndLatch) throws IOException {
        if (initialHand.size() != HAND_SIZE) {
            throw new IllegalArgumentException("Initial hand must contain exactly 4 cards");
        }
        this.playerId = id;
        this.hand = new ArrayList<>(initialHand);
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.gameOver = gameOver;
        this.gameEndLatch = gameEndLatch;
        this.outputWriter = new FileWriter("player" + id + "_output.txt");
        writeToFile("player " + id + " initial hand " + formatHand());
    }

    /*
     * Write a message to the player's output file
     */
    private void writeToFile(String message) throws IOException {
        synchronized (outputWriter) {
            outputWriter.write(message + "\n");
            outputWriter.flush();
        }
    }

    /* Formatting the player's hand as a space-separated string. */
    private String formatHand() {
        synchronized (handLock) {
            return hand.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "));
        }
    }

    /*
     * Checking if the player's hand includes all cards of the smae value,
     * indicating a win i
     */
    protected boolean checkWinningHand() {
        synchronized (handLock) {
            Map<Integer, Integer> counts = new HashMap<>();
            for (int card : hand) {
                counts.merge(card, 1, Integer::sum);
                if (counts.get(card) == HAND_SIZE) {
                    return true;
                }
            }
            return false;
        }
    }

    /*
     * Selecting a card to discard, preferring cards that don't match the player's
     * ID
     */
    private int selectCardToDiscard() {
        synchronized (handLock) {
            // Prefer discarding non-preferred cards
            for (int card : hand) {
                if (card != playerId) {
                    return card;
                }
            }
            return hand.get(0);
        }
    }

    /*
     * Method to perform a single turn,draws a card, checks for a winning hand, and
     * discards a card.
     */
    private void performTurn() throws IOException, InterruptedException {
        synchronized (handLock) {
            // Draw a card from the left deck
            int drawnCard = leftDeck.drawFromTop();
            hand.add(drawnCard);
            writeToFile("player " + playerId + " draws a " + drawnCard +
                    " from deck " + leftDeck.getDeckId());

            // Check if the player has a winning hand.
            if (checkWinningHand()) {
                if (gameOver.compareAndSet(false, true)) {
                    // Maintain hand size by discarding a card before exiting.
                    int discardCard = selectCardToDiscard();
                    hand.remove(Integer.valueOf(discardCard));
                    rightDeck.addToBottom(discardCard);
                    writeToFile("player " + playerId + " discards a " + discardCard +
                            " to deck " + rightDeck.getDeckId());
                    handleWin();
                    return;
                }
            }

            // Discard a card to the right deck
            int discardCard = selectCardToDiscard();
            hand.remove(Integer.valueOf(discardCard));
            rightDeck.addToBottom(discardCard);
            writeToFile("player " + playerId + " discards a " + discardCard +
                    " to deck " + rightDeck.getDeckId());
            writeToFile("player " + playerId + " current hand is " + formatHand());
        }
    }

    /* The main method executed when the player's thread starts */
    @Override
    public void run() {
        try {
            // Check for an initial winning hand.
            if (checkWinningHand()) {
                if (gameOver.compareAndSet(false, true)) {
                    handleWin();
                    return;
                }
            }

            while (!gameOver.get()) {
                // Performing turns until the game is over
                performTurn();
                Thread.sleep(10); // Prevent busy waiting
            }

            // To handle non-winner exit if the game ends
            if (!hasExited) {
                handleNonWinnerExit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Handling actions when the player wins/* */
    private void handleWin() throws IOException {
        System.out.println("player " + playerId + " wins");
        writeToFile("player " + playerId + " wins");
        writeToFile("player " + playerId + " exits");
        writeToFile("player " + playerId + " final hand: " + formatHand());
        hasExited = true;
        gameEndLatch.countDown();
    }

    /* Handles actions when a non-winner exits the game */
    private void handleNonWinnerExit() throws IOException {
        if (!hasExited) {
            writeToFile("player " + playerId + " exits");
            writeToFile("player " + playerId + " final hand: " + formatHand());
            hasExited = true;
        }
    }

    /* Returns a copy of the player's hand */
    public List<Integer> getHand() {
        synchronized (handLock) {
            return new ArrayList<>(hand);
        }
    }

    /* Returns the player's ID as a string. */
    public String getPlayerId() {
        return String.valueOf(playerId);
    }
}