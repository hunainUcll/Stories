package be.ucll.unit.model;

import be.ucll.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    // Happy tests
    @Test
    void givenValidBookDetails_whenCreatingBook_thenBookIsCreated() {
        assertDoesNotThrow(() -> new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5));
    }

    @Test
    void givenValidBookWithMultipleCopies_whenCreatingBook_thenBookIsCreated() {
        assertDoesNotThrow(() -> new Book("The Hobbit", "J.R.R. Tolkien", "978-1-56619-909-4", 1937, 10));
    }

    @Test
    void givenValidRecentPublicationYear_whenCreatingBook_thenBookIsCreated() {
        assertDoesNotThrow(() -> new Book("New Book", "Author Name", "978-3-16-148410-0", 2024, 3));
    }

    // Unhappy tests (More cases added)

    @Test
    void givenEmptyTitle_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("", "J.K. Rowling", "978-0-545-01022-1", 2001, 5));
        assertEquals("title is required.", exception.getMessage());
    }

    @Test
    void givenNullTitle_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book(null, "J.K. Rowling", "978-0-545-01022-1", 2001, 5));
        assertEquals("title is required.", exception.getMessage());
    }

    @Test
    void givenEmptyAuthor_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "", "978-0-545-01022-1", 2001, 5));
        assertEquals("author is required.", exception.getMessage());
    }

    @Test
    void givenNullAuthor_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", null, "978-0-545-01022-1", 2001, 5));
        assertEquals("author is required.", exception.getMessage());
    }

    @Test
    void givenEmptyISBN_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "J.K. Rowling", "  ", 2001, 5));
        assertEquals("ISBN is required.", exception.getMessage());
    }

    @Test
    void givenNullISBN_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "J.K. Rowling", null, 2001, 5));
        assertEquals("ISBN is required.", exception.getMessage());
    }

    @Test
    void givenWrongISBNFormat_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "J.K. Rowling", "123456789", 2001, 5));
        assertEquals("ISBN has wrong format", exception.getMessage());
    }

    @Test
    void givenNegativePublicationYear_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", -1999, 5));
        assertEquals("Publication year must be a positive integer.", exception.getMessage());
    }

    @Test
    void givenFuturePublicationYear_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2030, 5));
        assertEquals("Publication year cannot be in the future.", exception.getMessage());
    }


    @Test
    void givenNegativeCopies_whenCreatingBook_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> new Book("The Hobbit", "J.R.R. Tolkien", "978-1-56619-909-4", 1937, -5));
        assertEquals("Available copies must be a positive number.", exception.getMessage());
    }

}