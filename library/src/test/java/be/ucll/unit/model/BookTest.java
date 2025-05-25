package be.ucll.unit.model;

import be.ucll.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testValidBook() {
        // Happy path: all fields are valid
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 5);
        assertEquals("Clean Code", book.getTitle());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals("9780132350884", book.getIsbn());
        assertEquals(2008, book.getPublicationYear());
        assertEquals(5, book.getAvailableCopies());
    }

    @Test
    public void testInvalidAvailableCopies() {
        // Unhappy path: available copies are negative
        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, -1);
        });
        assertEquals("Available copies must be a positive number.", exception.getMessage());
    }


}