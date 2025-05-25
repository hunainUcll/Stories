package be.ucll.unit.model;

import be.ucll.model.User; // Correct import
import be.ucll.model.Book; // Correct import
import be.ucll.model.Magazine; // Correct import
import be.ucll.model.Publication; // Correct import
import be.ucll.model.Loan; // Correct import
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    @Test
    public void testValidLoan() {
        // Happy path: all fields are valid
        User user = new User("John Doe", "password123", "john.doe@example.com", 25);
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 5);
        Magazine magazine = new Magazine("National Geographic", "Susan Goldberg", "1234-5678", 2022, 10);
        List<Publication> publications = Arrays.asList(book, magazine);

        Loan loan = new Loan(user, publications, LocalDate.now());

        assertEquals(user, loan.getUser());
        assertEquals(publications, loan.getPublications());
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(LocalDate.now().plusDays(30), loan.getEndDate());

        // Check available copies after lending
        assertEquals(4, book.getAvailableCopies());
        assertEquals(9, magazine.getAvailableCopies());

        // Return the publications
        loan.returnPublications();

        // Check available copies after returning
        assertEquals(5, book.getAvailableCopies());
        assertEquals(10, magazine.getAvailableCopies());
    }

    @Test
    public void testInvalidUser() {
        // Unhappy path: user is null
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 5);
        List<Publication> publications = Arrays.asList(book);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Loan(null, publications, LocalDate.now());
        });
        assertEquals("User is required.", exception.getMessage());
    }

    @Test
    public void testInvalidPublications() {
        // Unhappy path: publications list is null
        User user = new User("John Doe", "password123", "john.doe@example.com", 25);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Loan(user, null, LocalDate.now());
        });
        assertEquals("List is required.", exception.getMessage());

        // Unhappy path: publications list is empty
        exception = assertThrows(RuntimeException.class, () -> {
            new Loan(user, List.of(), LocalDate.now());
        });
        assertEquals("List is required.", exception.getMessage());
    }

    @Test
    public void testInvalidStartDate() {
        // Unhappy path: start date is null
        User user = new User("John Doe", "password123", "john.doe@example.com", 25);
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 5);
        List<Publication> publications = Arrays.asList(book);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Loan(user, publications, null);
        });
        assertEquals("Start date is required.", exception.getMessage());

        // Unhappy path: start date is in the future
        exception = assertThrows(RuntimeException.class, () -> {
            new Loan(user, publications, LocalDate.now().plusDays(1));
        });
        assertEquals("Start date cannot be in the future.", exception.getMessage());
    }

    @Test
    public void testNoAvailableCopies() {
        // Unhappy path: no available copies for a publication
        User user = new User("John Doe", "password123", "john.doe@example.com", 25);
        Book book = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 0);
        List<Publication> publications = Arrays.asList(book);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            new Loan(user, publications, LocalDate.now());
        });
        assertEquals("No copies available for Clean Code", exception.getMessage());
    }
}