package be.ucll.unit.model;

import be.ucll.model.Magazine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MagazineTest {

    // Happy tests
    @Test
    void givenValidMagazineDetails_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("Time", "John Doe", "1234-5678", 2022, 7);

        assertEquals("Time", magazine.getTitle());
        assertEquals("John Doe", magazine.getEditor());
        assertEquals("1234-5678", magazine.getISSN());
        assertEquals(2022, magazine.getPublicationYear());
        assertEquals(7, magazine.getAvailableCopies());
    }

    @Test
    void givenValidMagazineWithMultipleCopies_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("National Geographic", "Jane Smith", "9876-5432", 1995, 15);

        assertEquals("National Geographic", magazine.getTitle());
        assertEquals("Jane Smith", magazine.getEditor());
        assertEquals("9876-5432", magazine.getISSN());
        assertEquals(1995, magazine.getPublicationYear());
        assertEquals(15, magazine.getAvailableCopies());
    }

    @Test
    void givenValidRecentPublicationYear_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("Tech Monthly", "Editor Name", "1023-4567", 2024, 2);

        assertEquals("Tech Monthly", magazine.getTitle());
        assertEquals("Editor Name", magazine.getEditor());
        assertEquals("1023-4567", magazine.getISSN());
        assertEquals(2024, magazine.getPublicationYear());
        assertEquals(2, magazine.getAvailableCopies());
    }

    // Unhappy tests (More cases added)

    @Test
    void givenEmptyTitle_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("", "John Doe", "1234-5678", 2022, 7));
        assertEquals("title is required.", exception.getMessage());
    }

    @Test
    void givenNullTitle_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine(null, "John Doe", "1234-5678", 2022, 7));
        assertEquals("title is required.", exception.getMessage());
    }

    @Test
    void givenEmptyEditor_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "", "1234-5678", 2022, 7));
        assertEquals("editor is required.", exception.getMessage());
    }

    @Test
    void givenNullEditor_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", null, "1234-5678", 2022, 7));
        assertEquals("editor is required.", exception.getMessage());
    }

    @Test
    void givenEmptyISSN_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "John Doe", "   ", 2022, 7));
        assertEquals("ISSN is required.", exception.getMessage());
    }

    @Test
    void givenWrongISSNFormat_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "John Doe", "12345678", 2022, 7));
        assertEquals("ISSN has wrong format", exception.getMessage());
    }

    @Test
    void givenFuturePublicationYear_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "John Doe", "1234-5678", 2030, 7));
        assertEquals("Publication year cannot be in the future.", exception.getMessage());
    }

    @Test
    void givenNegativeCopies_whenCreatingMagazine_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "John Doe", "1234-5678", 2022, -3));
        assertEquals("Available copies must be a positive number.", exception.getMessage());
    }
}