
// CardGameTest.java
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/* Test class for verifying the functionality of the CardGame class*/
public class CardGameTest {

    // test the intilaization of the CardGame class with valid inputs
    @Test
    public void testInitialization() throws IOException {
        // create a new example of CardGame
        CardGame game = new CardGame();
        // prepare a pack of cards for testing
        List<Integer> pack = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2);

        // Use reflection to set the pack
        try {
            java.lang.reflect.Field packField = CardGame.class.getDeclaredField("pack");
            packField.setAccessible(true);
            packField.set(game, pack);
        } catch (Exception e) {
            fail("Failed to set pack field");
        }

        // Start the game with two players
        game.initializeGame(2);

        // Use reflection to get players and decks
        try {
            java.lang.reflect.Field playersField = CardGame.class.getDeclaredField("players");
            java.lang.reflect.Field decksField = CardGame.class.getDeclaredField("decks");
            playersField.setAccessible(true);
            decksField.setAccessible(true);

            List<?> players = (List<?>) playersField.get(game);
            List<?> decks = (List<?>) decksField.get(game);

            // Verify the number of players and decks.
            assertEquals("Should have 2 players", 2, players.size());
            assertEquals("Should have 2 decks", 2, decks.size());
        } catch (Exception e) {
            fail("Failed to access fields");
        }
    }

    // Test for invalid player count during intialization
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPlayerCount() throws IOException {
        CardGame game = new CardGame();
        // Attempt to intialize the game with 1 player(invalid)
        game.initializeGame(1);
    }
}