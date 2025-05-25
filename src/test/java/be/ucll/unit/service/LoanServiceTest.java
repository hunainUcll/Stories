package be.ucll.unit.service;

import be.ucll.model.*;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.LoanService;
import be.ucll.unit.repository.LoanRepositoryStub;
import be.ucll.unit.repository.PublicationRepositoryStub;
import be.ucll.unit.repository.UserRepositoryStub;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LoanServiceTest {

    private LoanService loanService;
    private UserRepositoryStub userRepository;
    private PublicationRepository publicationRepository;
    private LoanRepository loanRepository;


    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryStub();
        publicationRepository = new PublicationRepositoryStub();
        loanRepository = new LoanRepositoryStub();
        loanService = new LoanService(loanRepository, userRepository,publicationRepository);
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

    // story 30
    @Test
    void givenPublicationIdDoesNotExist_whenSetLoanForUser_thenThrowsPublicationNotFoundException() {
        User user =   new User("29Savage", 25, "29.savage@ucll.be", "john1234");
        userRepository.save(user);
        LocalDate startDate = LocalDate.now();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanService.setLoanForUser(user.getEmail(), startDate, List.of(999L))
        );

        assertEquals("Publication with id 999 not found.", ex.getMessage());
    }

    // making a rubbish test to see if my publications are getting delivered
    // there are too many errors making a test to test weather the problem is my repo or the other tests
    @Test
    void givenPublicationId_whenFindById_thenPublicationIsFound() {
        Long publicationId = 1L;
        Optional<Publication> publicationOpt = publicationRepository.findById(publicationId);

        assertTrue(publicationOpt.isPresent(), "Publication with ID 1L should be found");
        assertEquals("Harry Potter", publicationOpt.get().getTitle(), "Publication title should match");
    }

    @Test
    void givenUserEmailDoesNotExist_whenSetLoanForUser_thenThrowsUserNotFoundException() {
        LocalDate startDate = LocalDate.now();
        List<Long> publicationIds = List.of(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanService.setLoanForUser("notfound@example.com", startDate, publicationIds)
        );

        assertEquals("User not found.", ex.getMessage());
    }

    @Test
    void givenUserHasActiveLoan_whenSetLoanForUser_thenThrowsActiveLoanExistsException() {
        User foundUser = userRepository.findUserByEmail("21.savage@ucll.be");

        Publication foundPub = publicationRepository.findById(1L).orElseThrow();
        Loan existing = new Loan(foundUser, List.of(foundPub), LocalDate.now().minusDays(1)); // active loan
        loanRepository.save(existing);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanService.setLoanForUser(foundUser.getEmail(), LocalDate.now(), List.of(foundPub.getId()))
        );

        assertEquals("User already has an active loan.", ex.getMessage());
    }
    @Test
    void givenStartDateIsInFuture_whenSetLoanForUser_thenThrowsConstraintViolationException() {

        User foundUser = userRepository.findUserByEmail("21.savage@ucll.be");
        Publication foundPub = publicationRepository.findById(1L).orElseThrow();

        LocalDate future = LocalDate.now().plusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
                loanService.setLoanForUser(foundUser.getEmail(), future, List.of(foundPub.getId()))
        );
    }
    @Test
    void givenPublicationHasNoCopiesLeft_whenSetLoanForUser_thenThrowsNoAvailableCopiesException() {
        User foundUser = userRepository.findUserByEmail("23.savage@ucll.be");
        Publication foundPub = publicationRepository.findById(5L).orElseThrow();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanService.setLoanForUser(foundUser.getEmail(), LocalDate.now(), List.of(foundPub.getId()))
        );

        assertEquals("Unable to lend publication. No copies available for Timelessssss.", ex.getMessage());
    }











}
