package be.ucll.unit.service;

import be.ucll.model.*;
import be.ucll.repository.*;
import be.ucll.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock LoanRepository        loanRepo;
    @Mock UserRepository        userRepo;
    @Mock PublicationRepository pubRepo;
    @Mock MembershipRepository  membershipRepo;

    @InjectMocks LoanService loanService;

    private User john;
    private Publication book;

    @BeforeEach
    void init() {
        john = new User("John Doe", "pw", "john@ucll.be", 25);
        book = new Book("Soft Skills", "J. Sonmez", "9780137081073", 2014, 3);
    }

    /* ---------------------------------------------------------
     * register loan – happy path
     * ------------------------------------------------------- */
    @Test
    void registerLoanHappyPath() {
        when(userRepo.findByEmail(john.getEmail()))
                .thenReturn(Optional.of(john));
        when(pubRepo.findById(1L)).thenReturn(Optional.of(book));
        when(loanRepo.findActiveLoansByUserEmail(john.getEmail(), LocalDate.now()))
                .thenReturn(List.of());
        when(loanRepo.save(any(Loan.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Loan loan = loanService.registerLoan(john.getEmail(),
                LocalDate.now(),
                List.of(1L));

        assertEquals(john, loan.getUser());
        verify(loanRepo).save(loan);
    }

    /* ---------------------------------------------------------
     * register loan – already active
     * ------------------------------------------------------- */
    @Test
    void registerLoanWhenActiveLoanExistsThrows() {
        when(userRepo.findByEmail(john.getEmail()))
                .thenReturn(Optional.of(john));
        when(loanRepo.findActiveLoansByUserEmail(john.getEmail(), LocalDate.now()))
                .thenReturn(List.of(mock(Loan.class)));     // one active -> block

        assertThrows(ResponseStatusException.class,
                () -> loanService.registerLoan(john.getEmail(),
                        LocalDate.now(),
                        List.of()));
        verify(loanRepo, never()).save(any());
    }
}
