package be.ucll.unit.model;

import be.ucll.model.Book;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    // Happy tests

    @Test
    void givenValidBookDetails_whenCreatingBook_thenBookHasExpectedValues() {
        Book book = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);

        assertEquals("Harry Potter", book.getTitle());
        assertEquals("J.K. Rowling", book.getAuthor());
        assertEquals("978-0-545-01022-1", book.getISBN());
        assertEquals(2001, book.getPublicationYear());
        assertEquals(5, book.getAvailableCopies());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenValidBookWithMultipleCopies_whenCreatingBook_thenBookHasExpectedValues() {
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", "978-1-56619-909-4", 1937, 10);

        assertEquals("The Hobbit", book.getTitle());
        assertEquals("J.R.R. Tolkien", book.getAuthor());
        assertEquals("978-1-56619-909-4", book.getISBN());
        assertEquals(1937, book.getPublicationYear());
        assertEquals(10, book.getAvailableCopies());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenValidRecentPublicationYear_whenCreatingBook_thenBookHasExpectedValues() {
        Book book = new Book("New Book", "Author Name", "978-3-16-148410-0", 2024, 3);

        assertEquals("New Book", book.getTitle());
        assertEquals("Author Name", book.getAuthor());
        assertEquals("978-3-16-148410-0", book.getISBN());
        assertEquals(2024, book.getPublicationYear());
        assertEquals(3, book.getAvailableCopies());

        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.isEmpty());
    }

    // Unhappy tests

    @Test
    void givenEmptyTitle_whenValidatingBook_thenValidationFails() {
        Book book = new Book("", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("title") &&
                        v.getMessage().equals("title is required.")
        ));
    }

    @Test
    void givenNullTitle_whenValidatingBook_thenValidationFails() {
        Book book = new Book(null, "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("title is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenEmptyAuthor_whenValidatingBook_thenValidationFails() {
        Book book = new Book("Harry Potter", "", "978-0-545-01022-1", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("author is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenNullAuthor_whenValidatingBook_thenValidationFails() {
        Book book = new Book("Harry Potter", null, "978-0-545-01022-1", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("author is required.", violations.iterator().next().getMessage());
    }

    // this is a very annoying problem
    // here i had to use AI because i had 2 error messages and iterator only returned one message even tho this test triggered 2 violations $
    // so ai suggested me to collect all the errors in a list and check for teh errors i was expecting to be in this list
    @Test
    void givenEmptyISBN_whenValidatingBook_thenValidationFails() {
        Book book = new Book("Harry Potter", "J.K. Rowling", "", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(2, violations.size());
        List<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();

        assertTrue(messages.contains("ISBN is required."));
    }


    @Test
    void givenNullISBN_whenValidatingBook_thenValidationFails() {
        Book book = new Book("Harry Potter", "J.K. Rowling", null, 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("ISBN is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenWrongISBNFormat_whenCreatingBook_thenThrowsRuntimeException() {
        Book book = new Book("Harry Potter", "J.K. Rowling", "123456789", 2001, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1,violations.size());
        assertEquals("ISBN has wrong format", violations.iterator().next().getMessage());
    }

    @Test
    void givenNegativePublicationYear_whenCreatingBook_thenThrowsRuntimeException() {
        Book book = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", -1999, 5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("year cannot be 0 or negative", violations.iterator().next().getMessage());
    }

    @Test
    void givenFuturePublicationYear_whenCreatingBook_thenThrowsRuntimeException() {
        int nextYear = java.time.Year.now().getValue() + 1;
        Exception exception = assertThrows(RuntimeException.class, () ->
                new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", nextYear, 5));
        assertEquals("Publication year cannot be in the future.", exception.getMessage());
    }

    @Test
    void givenNegativeCopies_whenValidatingBook_thenValidationFails() {
        Book book = new Book("The Hobbit", "J.R.R. Tolkien", "978-1-56619-909-4", 1937, -5);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertEquals(1, violations.size());
        assertEquals("Available copies must not be a negative number.", violations.iterator().next().getMessage());
    }
}
