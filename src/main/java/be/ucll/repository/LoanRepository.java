package be.ucll.repository;

import be.ucll.model.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LoanRepository {
    private final List<Loan> loans;

    public LoanRepository() {
        // Hardcoded Users
        User user1 = new User("21Savage", 25, "21.savage@ucll.be", "john1234");
        User user2 = new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234");

        // Hardcoded Publications
        Publication pub1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        Publication pub2 = new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5);

        // Assign Publications
        List<Publication> publications1 = new ArrayList<>();
        publications1.add(pub1); // For user1

        List<Publication> publications2 = new ArrayList<>();
        publications2.add(pub2); // For user2

        // Create Loans
        loans = new ArrayList<>(List.of(
                new Loan(user1, publications1, LocalDate.of(2025, 5, 8)),
                new Loan(user2, publications2, LocalDate.of(2025, 4, 15))
        ));
    }

    public List<Loan> getAllLoans() {
        return loans;
    }

    public List<Loan> findLoanByUser(String email, boolean onlyActive) {
        return loans.stream()
                .filter(loan -> loan.getUser().getEmail().equals(email))
                .filter(loan -> {
                    if (onlyActive) {
                        return !loan.isReturned() && !loan.getEndDate().isBefore(LocalDate.now());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    public void deleteUserLoans(String email) {
        List<Loan> userLoans = findLoanByUser(email, false);
        loans.removeAll(userLoans);
    }
}
