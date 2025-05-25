package be.ucll.unit.service;

import be.ucll.model.*;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.LoanService;
import be.ucll.unit.repository.LoanRepositoryStub;
import be.ucll.unit.repository.MembershipRepositoryStub;
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
    private MembershipRepository membershipRepository;


    @BeforeEach
    void setUp() {
        loanRepository = new LoanRepositoryStub();
        userRepository = new UserRepositoryStub();
        publicationRepository = new PublicationRepositoryStub();
        membershipRepository = new MembershipRepositoryStub();
        loanService = new LoanService(loanRepository, userRepository, publicationRepository,membershipRepository);

        // --- USERS ---
        User user1 = new User("21Savage", 25, "21.savage@ucll.be", "john1234");
        User user2 = new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234");
        User user3 = new User("Jack Doe", 30, "jack.doe@ucll.be", "jack1234");
        User user4 = new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234");
        User user5 = new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234");

        User user6 = new User("22Savage", 25, "22.savage@ucll.be", "john1234", new Profile(
                "Aspiring rapper with a love for tech.",
                "Antwerp",
                "Music, rapping, Basketball"
        ));

        User user7 = new User("23Savage", 25, "23.savage@ucll.be", "john1234", new Profile(
                "Aspiring rapper with a love for activities.",
                "Brussels",
                "Smoking, rapping, Coding"
        ));

        // Register users
        for (User user : List.of(user1, user2, user3, user4, user5, user6, user7)) {
            userRepository.save(user);
        }

        // --- PUBLICATIONS ---
        Book book1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        book1.setId(1L);
        Book book2 = new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5);
        book2.setId(2L);
        Book book3 = new Book("Potterrrr", "J.K. Rowling", "978-0-545-01032-2", 2001, 9);
        book3.setId(3L);
        publicationRepository.save(book1);
        publicationRepository.save(book2);
        publicationRepository.save(book3);

        Magazine mag1 = new Magazine("Time", "John Doe", "1234-5678", 2022, 7);
        mag1.setId(4L);
        Magazine mag2 = new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 23);
        mag2.setId(5L);
        Magazine mag3 = new Magazine("Timelessssss", "John Doe", "1234-5678", 2022, 0);
        mag3.setId(6L);
        publicationRepository.save(mag1);
        publicationRepository.save(mag2);
        publicationRepository.save(mag3);

        // --- LOANS ---
        Loan loan1 = new Loan(user1, List.of(book1), LocalDate.of(2025, 5, 24)); // active
        Loan loan2 = new Loan(user2, List.of(book2), LocalDate.of(2025, 4, 15)); // inactive (assume returned)
        Loan loan3 = new Loan(user3, List.of(book3), LocalDate.of(2025, 5, 23)); // active but returned
        loan3.setReturned(true);

        loanRepository.save(loan1);
        loanRepository.save(loan2);
        loanRepository.save(loan3);
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
            loanService.deleteUserLoans("sarah.doe@ucll.be");
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
        Publication foundPub = publicationRepository.findById(6L).orElseThrow();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                loanService.setLoanForUser(foundUser.getEmail(), LocalDate.now(), List.of(foundPub.getId()))
        );

        assertEquals("Unable to lend publication. No copies available for Timelessssss.", ex.getMessage());
    }

    @Test
    void givenNonExistentUser_whenReturnLoan_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.returnLoansForUser("notfound@ucll.be", LocalDate.now());
        });

        assertEquals("User does not exist.", ex.getMessage());
    }

    @Test
    void givenUserWithNoActiveLoan_whenReturnLoan_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.returnLoansForUser("jane.toe@ucll.be", LocalDate.now());
        });

        assertEquals("User has no active loan", ex.getMessage());
    }

    // basically testing for active loans but they are returned already
    @Test
    void givenUserWithAlreadyReturnedLoan_whenReturnLoan_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.returnLoansForUser("jack.doe@ucll.be", LocalDate.now());
        });

        assertEquals("Loan has already been returned.", ex.getMessage());
    }

    @Test
    void givenReturnDateBeforeStartDate_whenReturnLoan_thenThrowsException() {

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.returnLoansForUser("21.savage@ucll.be", LocalDate.now().minusDays(50));
        });

        assertEquals("Invalid return date. Return date must be after start date.", ex.getMessage());
    }

    @Test
    void givenNullReturnDate_whenReturnLoan_thenThrowsException() {
        User user = userRepository.findUserByEmail("21.savage@ucll.be");
        Publication pub = publicationRepository.findById(1L).orElseThrow();

        Loan activeLoan = new Loan(user, List.of(pub), LocalDate.now().minusDays(5));
        activeLoan.setEndDate(LocalDate.now().plusDays(5));
        loanRepository.save(activeLoan);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            loanService.returnLoansForUser(user.getEmail(), null);
        });

        assertEquals("Invalid return date. Return date must be after start date.", ex.getMessage());
    }











}
