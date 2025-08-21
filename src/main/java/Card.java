// The card class represents a playing card with a specific value
public class Card {
    // The value of the card
    private final int value;

    /*
     * Constructor to start a card object with a specific value
     */
    public Card(int value) {
        // Assign the provided value
        this.value = value;
    }

    /* get method to return the value of teh card */
    public int getValue() {
        return value;
    }

    /* toString() method to provide a string representation of the card. */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}