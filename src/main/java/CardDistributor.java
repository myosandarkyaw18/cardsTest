import java.util.ArrayList;
import java.util.List;

/**
 * CardDistributor handles the distribution and management of cards among
 * players.
 * This class ensures thread-safe operations through immutable fields and
 * defensive copying.
 */
public class CardDistributor {
    private final List<Integer> pack;
    private final int numPlayers;
    private final List<List<Integer>> playerHands;
    private final List<List<Integer>> decks;

    /**
     * Initializes a new CardDistributor with the given pack of cards and number of
     * players.
     */
    public CardDistributor(List<Integer> pack, int numPlayers) {
        this.pack = new ArrayList<>(pack);
        this.numPlayers = numPlayers;

        // Intialize lists to store hands and decks with initial capacity
        this.playerHands = new ArrayList<>(numPlayers);
        this.decks = new ArrayList<>(numPlayers);

        for (int i = 0; i < numPlayers; i++) {
            playerHands.add(new ArrayList<>());
            decks.add(new ArrayList<>());
        }

        distributeCards();
    }

    /**
     * Private method that handles the distribution of cards
     * 1: each player receives 4 cards for their initial hand
     * 2: remaining cards are distributed to players' decks in
     * round-robin fashion
     */
    private void distributeCards() {
        int currentIndex = 0;

        // Distribute 4 cards to each player
        for (int card = 0; card < 4; card++) {
            for (int player = 0; player < numPlayers; player++) {
                playerHands.get(player).add(pack.get(currentIndex++));
            }
        }

        // Fill decks with remaining cards
        while (currentIndex < pack.size()) {
            for (int deck = 0; deck < numPlayers && currentIndex < pack.size(); deck++) {
                decks.get(deck).add(pack.get(currentIndex++));
            }
        }
    }

    /**
     * Returns a defensive copy of a player's hand.
     * Uses 1-based indexing for player numbers (player 1 is index 1).
     */
    public List<Integer> getPlayerHand(int playerIndex) {
        // Validating player index is withinn acceptable range
        if (playerIndex < 1 || playerIndex > numPlayers) {
            throw new IllegalArgumentException("Invalid player index");
        }
        // return a copy of the player's hand
        return new ArrayList<>(playerHands.get(playerIndex - 1));
    }

    /**
     * Returns a copy of a specific player's deck.
     * Uses one-based indexing for deck numbers (deck 1 = index 1).
     *
     */
    public List<Integer> getDeckContents(int deckIndex) {
        if (deckIndex < 1 || deckIndex > numPlayers) {
            throw new IllegalArgumentException("Invalid deck index");
        }
        return new ArrayList<>(decks.get(deckIndex - 1));
    }
}