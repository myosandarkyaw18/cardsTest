import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * CardGame class manages the setup and execution of a card game
 * with multiple players and decks.
 */
public class CardGame {
    // Constants for game set up
    private static final int CARDS_PER_PLAYER = 4;
    private static final int CARDS_PER_DECK = 4;

    // To manage players, decks, and the pack of cards
    private final List<Player> players;
    private final List<Deck> decks;
    List<Integer> pack;
    private final AtomicBoolean gameOver;
    private final CountDownLatch gameEndLatch;
    private final List<Thread> playerThreads;

    // Constructor to initialzie the game components
    public CardGame() {
        this.players = new ArrayList<>();
        this.decks = new ArrayList<>();
        this.pack = new ArrayList<>();
        this.gameOver = new AtomicBoolean(false);
        this.gameEndLatch = new CountDownLatch(1);
        this.playerThreads = new ArrayList<>();
    }

    /** Method to start the game with the number of players */
    public void initializeGame(int n) throws IOException {
        // Validate the number of players
        if (n <= 0) {
            throw new IllegalArgumentException("Number of players must be positive");
        }

        // Validate the pack size. It must contain exactly n * 8 cards.
        if (pack.size() != n * 8) {
            throw new IllegalArgumentException(
                    String.format("Pack must contain exactly %d cards", n * 8));
        }

        // Create decks for each player
        for (int i = 0; i < n; i++) {
            decks.add(new Deck(i + 1));
        }

        // Deal initial hands to each palyer
        for (int i = 0; i < n; i++) {
            List<Integer> initialHand = new ArrayList<>();
            for (int j = 0; j < CARDS_PER_PLAYER; j++) {
                initialHand.add(pack.get(i + j * n));
            }
            players.add(new Player(i + 1, initialHand, decks.get(i),
                    decks.get((i + 1) % n), gameOver, gameEndLatch));
        }

        // Distribute remaining cards to decks
        int currentIndex = n * CARDS_PER_PLAYER;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < CARDS_PER_DECK && currentIndex < pack.size(); j++) {
                decks.get(i).addToBottom(pack.get(currentIndex++));
            }
        }
    }

    /**
     * Method to balance all decks by redistributing their cards evenly
     */
    private void balanceDecks() {
        try {
            List<Integer> allCards = new ArrayList<>();

            // Collect all cards from decks in order
            for (Deck deck : decks) {
                allCards.addAll(deck.getCards());
            }

            // Sort cards for consistent distribution
            Collections.sort(allCards);

            // Clear all decks to prepare for redistribution
            for (Deck deck : decks) {
                while (!deck.getCards().isEmpty()) {
                    deck.drawFromTop();
                }
            }

            // Redistribute cards across decks
            int cardsPerDeck = allCards.size() / decks.size();
            int cardIndex = 0;

            // ensure each deck gets at least the minimum required cards
            for (int i = 0; i < cardsPerDeck; i++) {
                for (Deck deck : decks) {
                    if (cardIndex < allCards.size()) {
                        deck.addToBottom(allCards.get(cardIndex++));
                    }
                }
            }

            // Distribute any remaining cards to decks with fewer than the maximum allowed
            while (cardIndex < allCards.size()) {
                for (Deck deck : decks) {
                    if (deck.getCards().size() < CARDS_PER_DECK && cardIndex < allCards.size()) {
                        deck.addToBottom(allCards.get(cardIndex++));
                    }
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Error while balancing decks: " + e.getMessage());
        }
    }

    /**
     * Method to start the game
     */
    public void startGame() {
        // Start a thread for each player
        for (Player player : players) {
            Thread t = new Thread(player);
            playerThreads.add(t);
            t.start();
        }

        try {
            gameEndLatch.await();
            Thread.sleep(500);

            for (Thread t : playerThreads) {
                t.join(500);
            }

            balanceDecks();

            for (Deck deck : decks) {
                deck.writeDeckContents();
            }

        } catch (InterruptedException | IOException e) {
            System.err.println("Error during game execution: " + e.getMessage());
            cleanup();
        }
    }

    /* Clean up game resources and reset states */
    private void cleanup() {
        // Interrupt and cleanup all running threads
        for (Thread t : playerThreads) {
            if (t != null && t.isAlive()) {
                t.interrupt();
            }
        }

        // Clear all game state
        playerThreads.clear();
        players.clear();
        decks.clear();
        pack.clear();
        gameOver.set(true);
    }

    public static void main(String[] args) {
        CardGame game = null;
        try (Scanner scanner = new Scanner(System.in)) {
            // Step 1: Validate number of players
            int n;
            while (true) {
                System.out.print("Enter number of players: ");
                if (!scanner.hasNextInt()) {
                    System.err.println("Error: Please enter a valid integer.");
                    scanner.next(); // Clear invalid input
                    continue;
                }

                n = scanner.nextInt();
                scanner.nextLine(); // Clear newline

                if (n <= 0) {
                    System.err.println("Error: Number of players must be positive.");
                    continue;
                }
                break; // Valid input received
            }

            // Step 2: Only proceed to pack file after valid player count
            List<Integer> pack = null;
            while (pack == null) {
                System.out.print("Enter pack location: ");
                String packLocation = scanner.nextLine();

                try {
                    File packFile = new File(packLocation);
                    if (!packFile.exists()) {
                        System.err.println("Error: File not found.");
                        continue;
                    }

                    pack = new ArrayList<>();
                    boolean invalidPack = false;
                    int lineNumber = 0;

                    try (Scanner fileScanner = new Scanner(packFile)) {
                        while (fileScanner.hasNextLine()) {
                            lineNumber++;
                            String line = fileScanner.nextLine().trim();
                            if (line.isEmpty())
                                continue;

                            try {
                                int cardValue = Integer.parseInt(line);
                                if (cardValue < 0) {
                                    System.err.println("Error: Negative number found at line " + lineNumber);
                                    invalidPack = true;
                                    break;
                                }
                                pack.add(cardValue);
                            } catch (NumberFormatException e) {
                                System.err.println("Error: Invalid number at line " + lineNumber);
                                invalidPack = true;
                                break;
                            }
                        }

                        if (invalidPack) {
                            pack = null;
                            continue;
                        }

                        // Validate pack size after successful read
                        int expectedCards = n * 8;
                        if (pack.size() != expectedCards) {
                            System.err.println(String.format(
                                    "Error: Pack must contain exactly %d cards for %d players (found %d cards).",
                                    expectedCards, n, pack.size()));
                            pack = null;
                            continue;
                        }
                    }

                    // Step 3: Start game only after all validation passes
                    try {
                        game = new CardGame();
                        game.pack = new ArrayList<>(pack);
                        game.initializeGame(n);
                        game.startGame();
                        System.out.println("Game Over!");
                    } catch (IOException e) {
                        System.err.println("Error during game execution: " + e.getMessage());
                        if (game != null) {
                            game.cleanup();
                        }
                    }

                } catch (IOException e) {
                    System.err.println("Error reading file: " + e.getMessage());
                    pack = null;
                    if (game != null) {
                        game.cleanup();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            if (game != null) {
                game.cleanup();
            }
        }
    }
}