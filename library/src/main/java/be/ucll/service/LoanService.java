package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import be.ucll.repository.MembershipRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
@Service
public class LoanService {

    private final LoanRepository        loanRepository;
    private final UserRepository        userRepository;
    private final PublicationRepository publicationRepository;
    private final MembershipRepository  membershipRepository;

    public LoanService(LoanRepository loanRepository,
                       UserRepository userRepository,
                       PublicationRepository publicationRepository, MembershipRepository membershipRepository) {
        this.loanRepository        = loanRepository;
        this.userRepository        = userRepository;
        this.publicationRepository = publicationRepository;
        this.membershipRepository  = membershipRepository;
    }

    @Transactional
    public Loan registerLoan(String email,
                             LocalDate startDate,
                             List<Long> publicationIds) {

        /* 1 user must exist */
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found."));

        /* 2 no active loan allowed */
        if (!loanRepository.findActiveLoansByUserEmail(email, startDate).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already has an active loan.");
        }


        /* 3 fetch publications – error on first missing id */
        List<Publication> pubs = publicationIds.stream()
                .map(id -> publicationRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Publication with id " + id + " not found.")))
                .collect(Collectors.toCollection(ArrayList::new));



        /* 4 create + save loan (Loan constructor does all domain checks) */
        Loan loan = new Loan(user, pubs, startDate);

        return loanRepository.save(loan);
    }

    public List<Loan> getLoansForUser(String email, boolean onlyActive) {
        // Check if user exists
        if (!userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        if (onlyActive) {
            return loanRepository.findActiveLoansByUserEmail(email, LocalDate.now());
        }

        return loanRepository.findByUserEmail(email);
    }

    public Loan addLoan(Loan loan) {
        // Validate and save using JPA's save()
        return loanRepository.save(loan);
    }

    public String deleteLoansByUserEmail(String email) {
        // 1. Check user exists
        if (!userRepository.existsByEmail(email)) { // Use proper JPA exists check
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist.");
        }

        // 2. Check for active loans
        List<Loan> userLoans = loanRepository.findByUserEmail(email);
        LocalDate today = LocalDate.now();
        boolean hasActiveLoans = userLoans.stream()
                .anyMatch(loan ->
                        !today.isBefore(loan.getStartDate()) &&
                                !today.isAfter(loan.getEndDate())
                );

        if (hasActiveLoans) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User has active loans.");
        }

        // 3. Delete using a custom query
        loanRepository.deleteByUserEmail(email);
        return "Loans of user successfully deleted.";
    }

    @Transactional
    public Loan returnLoan(String email, LocalDate returnDate) {

        /* 1 — basic validation */
        if (returnDate == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid return date. Return date must be after start date.");
        if (returnDate.isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Return date cannot be in the future.");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User does not exist."));

        /* 2 — user must have exactly one active (not yet returned) loan */
        List<Loan> activeLoans =
                loanRepository.findActiveLoansByUserEmail(email, returnDate)
                        .stream().filter(l -> !l.isReturned()).toList();

        if (activeLoans.isEmpty()) {
            // loan is past its planned end-date but still not returned
            activeLoans = loanRepository.findByUserEmailAndReturnedFalse(email);
        }
        Loan loan = activeLoans.get(0);

        if (loan.isReturned())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Loan has already been returned.");

        if (returnDate.isBefore(loan.getStartDate()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid return date. Return date must be after start date.");

        /* 3 — price calculation */
        BigDecimal price;

        Optional<Membership> optMembership =
                membershipRepository.findActiveMembership(email, returnDate);

        if (optMembership.isPresent()) {
            Membership membership = optMembership.get();

            if (membership.getFreeLoans() > 0) {       // free loan
                membership.redeemFreeLoan();
                price = BigDecimal.ZERO.setScale(2, RoundingMode.UNNECESSARY);

            } else {                                   // paid loan
                price = calcPrice(loan, membership.getType(), returnDate);
            }
        } else {                                       // no membership
            price = calcPrice(loan, "NONE", returnDate);
        }

        /* 4 — check for late return */
        LocalDate scheduledEnd = loan.getStartDate().plusDays(30);
        long daysLate = ChronoUnit.DAYS.between(scheduledEnd, returnDate);

        if (daysLate > 0) {
            BigDecimal fine = BigDecimal.valueOf(0.50)
                    .multiply(BigDecimal.valueOf(daysLate))
                    .multiply(BigDecimal.valueOf(loan.getPublications().size()));
            price = price.add(fine);
        }
        price = price.setScale(2, RoundingMode.HALF_UP);

        /* 4 — return publications & persist */
        loan.returnPublications();
        loan.markReturned(returnDate, price);
        return loanRepository.save(loan);
    }

    /* ---- helper: price per day table --------------------------------- */
    private static BigDecimal calcPrice(Loan loan,
                                        String type,
                                        LocalDate returnDate) {

        BigDecimal daily = switch (type) {
            case "BRONZE" -> BigDecimal.valueOf(0.75);
            case "SILVER" -> BigDecimal.valueOf(0.50);
            case "GOLD"   -> BigDecimal.valueOf(0.25);
            default       -> BigDecimal.ONE;          // NONE
        };

        long days = ChronoUnit.DAYS.between(loan.getStartDate(), returnDate);

        return daily
                .multiply(BigDecimal.valueOf(days))
                .multiply(BigDecimal.valueOf(loan.getPublications().size()))
                .setScale(2, RoundingMode.HALF_UP);   // ← **new**
    }
}