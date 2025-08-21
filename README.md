# Card Game Testing Documentation
This README provides an overview of the Ring Topology Card Game, detailing its rules, gameplay mechanics, and specific implementation strategy mentioned in the project specification. The game involves multiple players and decks, arranged in a ring topology, and the main goal is the first player who collects four cards of the same value wins the game. This is developed by Shin Thant Lwin & Myo Sandar Kyaw for ECM2414. This project was done as a pair programming assignment.

## Game Overview
Ring Topology Card Game with n players and n decks, implemented using Java threads. Players aim to collect four cards of the same value to win.The game has n players, indexed as player1, player2, player3, player4 and so  on.In our game, n = 1 to 5 is supported, so the game is played with 1-5 players and 1-5 decks . The number of decks and players must be the same. We manually created a pack file of (8n cards), reflecting the total players for 1 to 5 players. For Larger players scope, create a pack file of 8n cards, saying n being the players. Players and decks form a ring topology where each player draws cards from the top of a deck on their left and discards cards to the bottom of a deck on their right. The gameplay continues until a player achieves a winning hand which is four cards of the same value with certain card denominations, which reflect their index value as preference.

## Game Execution
1. Open terminal in project directory
2. Compile: `javac *.java`
3. Run: `javac CardGame.java && java CardGame`
4. Input:
   - Number of players: (players as wanted)
   - Pack file: packfor{n}players.txt


## Custom Pack Creation
While we provide pack files for 1-5 players, you can create your own pack files for larger player counts outside from our supported scope. To create a custom pack: Calculate required cards: 8n where n is the number of players and create a text file with exactly 8n lines, it is mandatory that each line should contain positive integer, ensuring values allow for possible winning combinations. 

## Test Suite Setup
### Requirements
- JDK 11+
- VS Code
- Extensions:
  - Maven for Java
  - Extension Pack for Java
- pom.xml file

### Verify Setup
1. VS Code Extensions:
   - Verify Maven for Java is installed
   - Verify Extension Pack for Java is installed
2. Project Structure:
   - pom.xml in root directory
   - /src/test/java/ contains test files
   - All test files have .java extension
3. Verify pom.xml contains:
   - JUnit dependencies
   - Required version numbers
   
### Test Files
Location: /src/test/java/
there should be the following files:
- CardGameTest.java 
- PlayerTest.java 
- DeckTest.java 
- CardDistributorTest.java
- EndGameTest.java 
- CardTest.java 
- GameConditionsTest.java

### Test Files Coverage
CardGameTest.java: to test the overall game flow, checking game initialization with 4 players, valid or invalid pack file handling, game completion scenarios, output file generation for players and decks and thread coordination.

PlayerTest.java: to test player moves and, verifying drawing and discarding cards, thread safety of player actions, Player preferences for cards, winning game condition, players management and exiting the game.

DeckTest.java: to test deck operations, testing thread-safe adding and removing cards, deck initialization, concurrent access handling, the way the cards are sorted out from top to bottom for decks, deck state management.

CardDistributorTest.java: to test the way the cards are distributed in round-robin to ensure players and decks are correctly set up, the players and decks get correct numbers of cards, distribution order and pack file reading and error handling for potential invalid inputs and distribution.

EndGameTest.java: to test the end-game logic,testing game termination, card final state and player final hands, final output contents and winner notifies the game win and cleanup.

CardTest.java: to test the card functionality, including value assignment, immutability, and comparisons, card state.

GameConditionsTest.java: to test the game rules and conditions, correct winner declaration,player synchronization, thread coordination, game state and game conditions handling.


### Test Dependencies
Some tests in the game may depend on others: CardGame tests require working with Card, Player, and Deck implementations, Player tests require working Card and Deck implementations. Full System Functionality is verified.

## Running Tests
1. Open CardGame project in VS Code
2. Click "Testing" icon in sidebar
3. Execute tests:
   - Full suite: Click top play button
   - Single class: Click play button next to class
   - Single method: Click play button next to method

## File Management
Test files created/deleted:
- player1_output.txt
- player2_output.txt

## Verification
### Expected Output
- Green checkmarks (✓) for passed tests
- Test Explorer showing all tests passed
- No red X marks are expected.
- "BUILD SUCCESS" in console output.
- Complete test summary

### Success Criteria
1. All tests passed (green checkmarks)
2. No compilation errors
3. Test output files generated
4. Clean console output
5. Build Success message

## Troubleshooting
Potential issues and solutions:
- "FileNotFoundException": Verify pack.txt location
- "TestNotFound": Ensure correct test directory
- Build failures: Check JUnit version mentioned