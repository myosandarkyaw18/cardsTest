import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/* Testing class for verifying the behaviour of the CardDistributor class */
public class CardDistributorTest {
    // Test case to verify the correct distribution of cards to palyers and decks
    @Test
    public void testDistribution() {
        // Creating a pck of cards with predictable values such as 1, 2 for testing
        List<Integer> pack = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2);
        CardDistributor distributor = new CardDistributor(pack, 2);

        // Start the CardDistributer with 2 players.
        List<Integer> player1Hand = distributor.getPlayerHand(1);
        List<Integer> player2Hand = distributor.getPlayerHand(2);

        // Assert that each palyer received precisely 4 cards
        assertEquals(4, player1Hand.size());
        assertEquals(4, player2Hand.size());

        // Verify the hands contain the expected cards for each player
        assertArrayEquals(new Integer[] { 1, 1, 1, 1 }, player1Hand.toArray());
        assertArrayEquals(new Integer[] { 2, 2, 2, 2 }, player2Hand.toArray());

        // Retrieve the contents of deck 1 and deck 2
        List<Integer> deck1 = distributor.getDeckContents(1);
        List<Integer> deck2 = distributor.getDeckContents(2);

        // Assert that each deck contains precisely 4 cards
        assertEquals(4, deck1.size());
        assertEquals(4, deck2.size());
    }
}