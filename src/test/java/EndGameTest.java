import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;

/* Test class for verifying the endgame validation logic  */
public class EndGameTest {

    // Test case for validating teh final state of the game
    @Test
    public void testValidateFinalState() throws IOException {
        List<Player> players = new ArrayList<>();
        List<Deck> decks = new ArrayList<>();
        List<Integer> pack = Arrays.asList(1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2);

        // Create two decks with 4 cards each
        for (int i = 0; i < 2; i++) {
            Deck deck = new Deck(i + 1);
            for (int j = 0; j < 4; j++) {
                deck.addToBottom(j + 1);
            }
            decks.add(deck);
        }

        AtomicBoolean gameOver = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        // Creating two players with 4 cards each
        for (int i = 0; i < 2; i++) {
            players.add(new Player(i + 1, Arrays.asList(1, 1, 1, 1),
                    decks.get(0), decks.get(1), gameOver, latch));
        }
        // validate the final state using the Endgame logic
        EndGame.validateFinalState(pack, decks, players);
    }
}