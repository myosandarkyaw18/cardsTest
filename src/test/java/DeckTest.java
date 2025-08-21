import static org.junit.Assert.*;

import org.junit.Test;

//Test class for verifying the functionality of the Deck class
public class DeckTest {

    // Test the basic operations of adding to the deck and drawing from the top
    @Test
    public void testDeckOperations() throws InterruptedException {
        // Create a Deck instance with ID 1.
        Deck deck = new Deck(1);

        // Add a card with value (5) to the bottom of the deck
        deck.addToBottom(5);

        // Draw the card from the top of teh deck and verify the value
        assertEquals(5, deck.drawFromTop());
    }

    // Test the retrieval of all cards in the deck.
    @Test
    public void testGetCards() {
        // Create a Deck instance with ID 1
        Deck deck = new Deck(1);

        // Add two cards to the bottom of the deck
        deck.addToBottom(1);
        deck.addToBottom(2);
        // Verify that the deck contains precisely 2 cards
        assertEquals(2, deck.getCards().size());
    }
}