import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/* Test class for verifying the functionality of Player class */
public class PlayerTest {
    private List<String> filesToDelete;

    // Before each test, setup method executed
    @Before
    public void setup() {
        filesToDelete = new ArrayList<>();
        filesToDelete.add("player1_output.txt");
        filesToDelete.add("player2_output.txt");
    }

    // After each test to delete generated files, clean up method executed
    @After
    public void cleanup() {
        // Delete any generated files after each test
        for (String fileName : filesToDelete) {
            try {
                Files.deleteIfExists(Paths.get(fileName));
            } catch (IOException e) {
                System.err.println("Failed to delete " + fileName);
            }
        }
    }

    // Test the intialization of a Player object
    @Test
    public void testPlayerInitialization() throws IOException {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        List<Integer> initialHand = Arrays.asList(1, 1, 1, 1);
        AtomicBoolean gameOver = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // Intialize the Player's Object
        Player player = new Player(1, initialHand, leftDeck, rightDeck, gameOver, latch);

        // Verify the player's hand is intialized correctly
        List<Integer> hand = player.getHand();
        assertEquals(4, hand.size());
        assertTrue(hand.containsAll(Arrays.asList(1, 1, 1, 1)));

        // Verify file contents of the output file
        String content = Files.readString(Paths.get("player1_output.txt"));
        assertTrue(content.contains("player 1 initial hand"));
    }

    // Test case that player correctly identifies a winning hand
    @Test
    public void testCheckWinningHand() throws IOException {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        List<Integer> winningHand = Arrays.asList(1, 1, 1, 1);
        AtomicBoolean gameOver = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // Intilaize the Player Object
        Player player = new Player(1, winningHand, leftDeck, rightDeck, gameOver, latch);

        // Verify that the Player identifies the hand as a winning hand
        assertTrue(player.checkWinningHand());
    }

    // Test case that teh Player correctly identifies a non-winning hand
    @Test
    public void testNonWinningHand() throws IOException {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        List<Integer> nonWinningHand = Arrays.asList(1, 1, 2, 1);
        AtomicBoolean gameOver = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // Intialize the Player object
        Player player = new Player(1, nonWinningHand, leftDeck, rightDeck, gameOver, latch);
        // Verify that the Player does not identify the hand as a winning hand.
        assertFalse(player.checkWinningHand());
    }
}