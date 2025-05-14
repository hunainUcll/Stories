package be.ucll.unit.service;

import be.ucll.model.Loan;
import be.ucll.repository.*;
import be.ucll.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class LoanServiceTest {

    private LoanService loanService;
    @BeforeEach
    void setUp() {
        PublicationRepository publicationRepository = new PublicationRepository();
        UserRepository userRepository = new UserRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository,publicationRepository);
        loanService = new LoanService(loanRepository,userRepository);
    }


    @Test
    public void givenExistingUser_whenGettingAllLoans_thenReturnsAllLoans() {
        List<Loan> loans = loanService.getLoansByUser("21.savage@ucll.be", true);

        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
    }

    @Test
    public void givenExistingUser_whenGettingOnlyActiveLoans_thenReturnsActiveLoans() {
        List<Loan> loans = loanService.getLoansByUser("21.savage@ucll.be", true);

        assertFalse(loans.isEmpty());
        // this is a bit tricky one to understand i never would have thought of this myself
        assertTrue(loans.stream().noneMatch(loan -> loan.getEndDate().isBefore(LocalDate.now())));
    }

    @Test
    public void givenUserWithNoLoans_whenGettingLoans_thenReturnsEmptyList() {
        List<Loan> loans = loanService.getLoansByUser("birgit.doe@ucll.be", false);

        assertTrue(loans.isEmpty());
    }

    // Story 16 happy test -
    @Test
    void givenExistingUserWithOnlyReturnedLoans_whenDeleteUserLoans_thenLoansDeletedSuccessfully() {
        String email = "jane.toe@ucll.be";
        String result = loanService.deleteUserLoans(email);
        assertEquals("Loans of user successfully deleted.", result);
    }


    //  Unhappy Tests like moi

    @Test
    public void givenNonExistentUser_whenGettingLoans_thenThrowsRuntimeExceptionWithCorrectMessage() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loanService.getLoansByUser("unknown@ucll.be", false);
        });

        assertEquals("User not found with given email", exception.getMessage());
    }

    @Test
    public void givenNullEmail_whenGettingLoans_thenThrowsRuntimeExceptionWithCorrectMessage() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            loanService.getLoansByUser(null, false);
        });

        assertEquals("User not found with given email", exception.getMessage());
    }

    @Test
    public void givenAllLoansAreInactive_whenGettingOnlyActiveLoans_thenReturnsEmptyList() {
        List<Loan> loans = loanService.getLoansByUser("jane.toe@ucll.be", true);

        assertTrue(loans.isEmpty());
    }

    // story 16 unhappy test

    @Test
    void givenNonExistentUser_whenDeleteUserLoans_thenThrowException() {
        // Given
        String nonExistentEmail = "ghost@ucll.be";

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans(nonExistentEmail);
        });

        assertEquals("User does not exist", ex.getMessage());
    }

    @Test
    void givenUserWithActiveLoans_whenDeleteUserLoans_thenThrowException() {
        // Given
        PublicationRepository publicationRepository = new PublicationRepository();
        UserRepository userRepository = new UserRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository,publicationRepository);
        String email = "21.savage@ucll.be";

        // Ensure the loan is still active (not returned and endDate not passed)
        List<Loan> loans = loanRepository.findLoanByUser(email, false);
        assertFalse(loans.isEmpty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans(email);
        });

        assertEquals("User has active loans.", ex.getMessage());
    }

    @Test
    void givenUserWithNoLoans_whenDeleteUserLoans_thenThrowException() {
        // Given
        PublicationRepository publicationRepository = new PublicationRepository();
        UserRepository userRepository = new UserRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository,publicationRepository);
        String email = "jack.doe@ucll.be"; // No loans associated

        // Sanity check
        assertTrue(loanRepository.findLoanByUser(email, false).isEmpty());

        // When + Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans(email);
        });

        assertEquals("User has no loans.", ex.getMessage());
    }

}
