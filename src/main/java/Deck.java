import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Deck class represents a deck of cards and
 * provides synchronized methods for managing cards
 */
public class Deck {
    private final LinkedList<Integer> cards;
    private final int deckId;
    private final Object lock;

    /**
     * Constructor to intialize a deck with specific ID
     */
    public Deck(int deckId) {
        this.deckId = deckId;
        this.cards = new LinkedList<>(); // use LinkedList for efficient card addition/ removal
        this.lock = new Object(); // Lock for synchronizing card operations.
    }

    /*
     * get method to return the deckID
     */
    public int getDeckId() {
        return deckId;
    }

    /** Add a card to the bottom of the deck */
    public void addToBottom(int card) {
        synchronized (lock) {
            cards.addLast(card);
            lock.notify();
        }
    }

    /**
     * Draws a card from the top of the deck and waits if the deck is empty
     *
     */
    public int drawFromTop() throws InterruptedException {
        synchronized (lock) {
            while (cards.isEmpty()) {
                lock.wait();
            }
            return cards.removeFirst();
        }
    }

    /**
     * returns a copy fo the current cards in the deck
     */
    public List<Integer> getCards() {
        synchronized (lock) {
            return new ArrayList<>(cards);
        }
    }

    /**
     * Write the contents of the deck to a file called "deck<ID>_output.txt".
     * EAch file contains the deckID and its current cards
     */
    public void writeDeckContents() throws IOException {
        // Use a FileWriter with the file named based on teh deck ID
        try (FileWriter writer = new FileWriter("deck" + deckId + "_output.txt")) {
            synchronized (lock) {
                StringBuilder sb = new StringBuilder();

                // Write the deck ID and its contents to the string builder
                sb.append("deck").append(deckId).append(" contents:");

                if (!cards.isEmpty()) {
                    sb.append(" ").append(cards.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(" ")));
                }
                // Write the string builder's contents to the file.
                writer.write(sb.toString());
            }
        }
    }
}