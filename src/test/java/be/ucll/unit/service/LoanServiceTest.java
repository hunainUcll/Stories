package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Magazine;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.LoanService;
import be.ucll.unit.repository.UserRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private LoanService loanService;
    private UserRepositoryStub userRepository;
//   private PublicationRepository publicationRepository;
    private LoanRepository loanRepository;


    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryStub();
   //     publicationRepository = new PublicationRepository();
       loanRepository = new LoanRepository();
        loanService = new LoanService(loanRepository, userRepository);
    }

    @Test
    public void givenExistingUser_whenGettingAllLoans_thenReturnsAllLoans() {
        List<Loan> loans = loanService.getLoansByUser("21.savage@ucll.be", false);
        assertFalse(loans.isEmpty());
        assertEquals(1, loans.size());
    }

    @Test
    public void givenExistingUser_whenGettingOnlyActiveLoans_thenReturnsActiveLoans() {
        List<Loan> loans = loanService.getLoansByUser("21.savage@ucll.be", true);
        assertFalse(loans.isEmpty());
        assertTrue(loans.stream().noneMatch(loan -> loan.getEndDate().isBefore(LocalDate.now())));
    }

    @Test
    public void givenUserWithNoLoans_whenGettingLoans_thenReturnsEmptyList() {
        List<Loan> loans = loanService.getLoansByUser("birgit.doe@ucll.be", false);
        assertTrue(loans.isEmpty());
    }

    @Test
    void givenExistingUserWithOnlyReturnedLoans_whenDeleteUserLoans_thenLoansDeletedSuccessfully() {
        String result = loanService.deleteUserLoans("jane.toe@ucll.be");
        assertEquals("Loans of user successfully deleted.", result);
    }

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

    @Test
    void givenNonExistentUser_whenDeleteUserLoans_thenThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans("ghost@ucll.be");
        });
        assertEquals("User does not exist", ex.getMessage());
    }

    @Test
    void givenUserWithActiveLoans_whenDeleteUserLoans_thenThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans("21.savage@ucll.be");
        });
        assertEquals("User has active loans.", ex.getMessage());
    }

    @Test
    void givenUserWithNoLoans_whenDeleteUserLoans_thenThrowException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.deleteUserLoans("jack.doe@ucll.be");
        });
        assertEquals("User has no loans.", ex.getMessage());
    }
}
