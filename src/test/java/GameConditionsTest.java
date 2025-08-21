import static org.junit.Assert.*;

import org.junit.Test;

// Test class for verifying the functionality of the GameConditions class
public class GameConditionsTest {
    @Test
    // Test basic game conditions, including the initial state and winner
    // declaration
    public void testBasicGameConditions() {
        // Create a GameConditions instance for a game with 2 players
        GameConditions conditions = new GameConditions(2);

        // To test initial state
        // the game should not be over, and no winner should be declared.
        assertFalse("Game should not be over initially", conditions.isGameOver());
        assertEquals("No winner should be set initially", -1, conditions.getWinner());

        // To test winner declaration
        // Player 1 declares win and player 2 attempts to declare a win after the game
        // is over
        conditions.declareWinner(1);
        conditions.declareWinner(2);

        // Assert that game is over and the winner is correctly set to player1
        assertTrue("Game should only be over after winner declared", conditions.isGameOver());
        assertEquals("Winner should be player 1", 1, conditions.getWinner());
    }

    /* Test player count intialization */
    @Test
    // Creating a gameConditions instance for a game with 2 players.
    public void testPlayerCount() {
        // To ensure there is only 2 players.
        GameConditions conditions = new GameConditions(2);
        assertEquals("Should have correct number of players", 2, conditions.getNumberOfPlayers());
    }
}