import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * GameConditons class that manages game-wide synchronization and state,
 * including game start, end, and winner declaration
 */
public class GameConditions {
    private final CyclicBarrier barrier;
    private final CountDownLatch gameEndLatch;
    private volatile boolean gameOver;
    private volatile int winner;
    private final int numberOfPlayers;

    /**
     * Constructor starts synchronization primitives and game state.
     */
    public GameConditions(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
        this.barrier = new CyclicBarrier(numberOfPlayers);
        this.gameEndLatch = new CountDownLatch(1);
        this.winner = -1;
        this.gameOver = false;
    }

    /**
     * Wait for the players to reach the starting point before the game starts
     * 
     * @throws InterruptedException
     */
    public void waitForAllPlayers() throws InterruptedException {
        try {
            barrier.await();
        } catch (BrokenBarrierException e) {
            throw new InterruptedException("Game start interrupted");
        }
    }

    /**
     * Allowing a player to declare themselves as the winner, ending the game
     */
    public boolean declareWinner(int playerId) {
        if (!gameOver && winner == -1) {
            winner = playerId;
            gameOver = true;
            gameEndLatch.countDown();
            return true;
        }
        return false;
    }

    /* Blocks the calling thread until the game ends */
    public void waitForGameEnd() throws InterruptedException {
        gameEndLatch.await();
    }

    /* Checking whether the game is over */
    public boolean isGameOver() {
        return gameOver;
    }

    /* get method to return the ID of the winner */
    public int getWinner() {
        return winner;
    }

    /* Get the total number of players in the game */
    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }
}