import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The EndGame class handles the cleanup and verfication steps at the end of a
 * card game
 */
public class EndGame {
    private final List<Player> players;
    private final List<Deck> decks;
    private static final int CARDS_PER_DECK = 4;

    /*
     * Constructor to start the EndGame handler with players and decks
     */
    public EndGame(List<Player> players, List<Deck> decks) {
        this.players = players;
        this.decks = decks;
    }

    /**
     * Constructor to handle the end game process, including verification and
     * writing final outputs
     */
    public void handleGameEnd() {
        try {
            // Allow time for final card movements
            Thread.sleep(100);

            // Verify player hands
            verifyPlayerHands();

            // Write final deck contents
            writeDeckContents();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /*
     * Verifies that each player has the expected number of cards in their hand.
     */
    private void verifyPlayerHands() {
        for (Player player : players) {
            List<Integer> hand = player.getHand();

            // Check if the deck hand size matches the expected value
            if (hand.size() != CARDS_PER_DECK) {
                // Print a warning if the hand size is incorrect
                System.err.println("Warning: Player " + player.getPlayerId() +
                        " has " + hand.size() + " cards instead of " + CARDS_PER_DECK);
            }
        }
    }

    /**
     * Write the final contents of all decks to their respective output files
     * checking that each deck has the expected number of cards
     */
    private void writeDeckContents() throws IOException {
        for (Deck deck : decks) {
            int deckSize = deck.getCards().size();
            // Check if the deck size matches the expected value.
            if (deckSize != CARDS_PER_DECK) {
                // Print a warning if the deck size is incorrect.
                System.err.println("Warning: Deck " + deck.getDeckId() +
                        " has " + deckSize + " cards instead of " + CARDS_PER_DECK);
            }
            deck.writeDeckContents();
        }
    }

    /**
     * Validating he final state of the game by comparing the original pack of cards
     * with the final distribution of cards among decks and players.
     */
    public static void validateFinalState(List<Integer> originalPack, List<Deck> decks, List<Player> players) {
        List<Integer> allCards = new ArrayList<>();

        // Add cards from decks
        for (Deck deck : decks) {
            allCards.addAll(deck.getCards());
        }

        // Add cards from player hands
        for (Player player : players) {
            List<Integer> hand = player.getHand();
            if (hand.size() != CARDS_PER_DECK) {
                System.err.println("Warning: Player " + player.getPlayerId() +
                        " final hand size: " + hand.size());
            }
            allCards.addAll(hand);
        }

        // Verify total card count
        if (allCards.size() != originalPack.size()) {
            System.err.println("Warning: Card count mismatch");
            System.err.println("Original pack size: " + originalPack.size());
            System.err.println("Final state cards: " + allCards.size());
        }
    }
}