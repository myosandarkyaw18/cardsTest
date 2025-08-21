import static org.junit.Assert.*;

import org.junit.Test;

/* Test class for verifying the functionality of the Card class  */
public class CardTest {

    // Test case to verify the creation of a Card Object and its methods
    @Test
    public void testCardCreation() {
        // Create a Card object with the value 5
        Card card = new Card(5);

        // Verify that the getValue() method returns the correct value.
        assertEquals(5, card.getValue());

        // Verify that toString() method returns the string representation of the card's
        // value.
        assertEquals("5", card.toString());
    }
}