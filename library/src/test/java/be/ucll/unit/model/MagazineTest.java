package be.ucll.unit.model;

import be.ucll.model.Magazine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MagazineTest {

    @Test
    public void testValidMagazine() {
        // Happy path: all fields are valid
        Magazine magazine = new Magazine("National Geographic", "Susan Goldberg", "1234-5678", 2022, 10);
        assertEquals("National Geographic", magazine.getTitle());
        assertEquals("Susan Goldberg", magazine.getEditor());
        assertEquals("1234-5678", magazine.getIssn());
        assertEquals(2022, magazine.getPublicationYear());
        assertEquals(10, magazine.getAvailableCopies());
    }

    @Test
    public void testInvalidAvailableCopies() {
        // Unhappy path: available copies is negative
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Magazine("National Geographic", "Susan Goldberg", "1234-5678", 2022, -1);
        });
        assertEquals("Available copies must be a positive number.", exception.getMessage());
    }

}