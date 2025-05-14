package be.ucll.unit.model;

import be.ucll.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

public class LoanTest {

    private User validUser;
    private Book validBook;
    private Magazine validMagazine;
    private ArrayList<Publication> publications;
    private Loan loan;



    @BeforeEach
     void setUp() {
         validUser = new User("John Doe", 30, "john.doe@ucll.be", "securePassword");
         validBook = new Book("Clean Code", "Robert C. Martin", "978-0-13-235088-4", 2008, 5);
         validMagazine = new Magazine("Tech Monthly", "Editor Name", "1234-5678", 2024, 3);
         publications = new ArrayList<>();  // Create a modifiable list
         publications.add(validBook);
         publications.add(validMagazine);
    }

    @Test
    void givenValidPublications_whenCreatingLoan_thenLoanHasExpectedValues() {
        Loan loan = new Loan(validUser, Arrays.asList(validBook,validMagazine), LocalDate.now());

        assertEquals(validUser, loan.getUser());
        assertEquals(publications, loan.getPublications());
        assertEquals(LocalDate.now(), loan.getStartDate());
        assertEquals(4, validBook.getAvailableCopies()); // Ensuring other publications remain unchanged
        assertEquals(2, validMagazine.getAvailableCopies()); // Ensuring other publications remain unchanged
    }

    @Test
    void givenActiveLoan_whenReturningPublications_thenLoanIsMarkedReturnedAndCopiesIncrease() {
        loan = new Loan(validUser, publications, LocalDate.now());

        loan.returnPublications();

        assertTrue(loan.isReturned());
        assertEquals(5, validBook.getAvailableCopies()); // Copies should be restored to original value
        assertEquals(3, validMagazine.getAvailableCopies()); // Copies should be restored to original value
    }



    @Test
    void givenNullUser_whenCreatingLoan_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(null, publications, LocalDate.now()));

        assertEquals("User is required.", exception.getMessage());
    }

    @Test
    void givenNullPublicationsList_whenCreatingLoan_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(validUser, null, LocalDate.now()));

        assertEquals("List is required.", exception.getMessage());
    }

    @Test
    void givenEmptyPublicationsList_whenCreatingLoan_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(validUser, Collections.emptyList(), LocalDate.now()));

        assertEquals("List is required.", exception.getMessage());
    }

    @Test
    void givenNullStartDate_whenCreatingLoan_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(validUser, publications, null));

        assertEquals("Start date is required.", exception.getMessage());
    }

    @Test
    void givenFutureStartDate_whenCreatingLoan_thenThrowsException() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(validUser, publications, futureDate));

        assertEquals("Start date cannot be in the future.", exception.getMessage());
    }

    @Test
    void givenPublicationWithNoAvailableCopies_whenCreatingLoan_thenThrowsException() {
        Book unavailableBook = new Book("The Art of Computer Programming", "Donald Knuth", "978-0-201-03801-1", 1968, 0);

        // did not use the predeclared list of publication instead imorted Arrays to use asLIst method to make a list wiht an invalid publication tto test the test basically
        Exception exception = assertThrows(RuntimeException.class,
                () -> new Loan(validUser, Arrays.asList(validBook, unavailableBook), LocalDate.now()));

        assertEquals("Unable to lend publication. No copies available for The Art of Computer Programming.", exception.getMessage());
        assertEquals(5, validBook.getAvailableCopies()); // Ensuring other publications remain unchanged
    }


    @Test
    void givenAlreadyReturnedLoan_whenReturningPublications_thenThrowsException() {
        loan = new Loan(validUser, publications, LocalDate.now());
        loan.returnPublications(); // First return
        Exception exception = assertThrows(RuntimeException.class, loan::returnPublications); // Second return should fail

        assertEquals("Publications have already been returned.", exception.getMessage());
    }
}

