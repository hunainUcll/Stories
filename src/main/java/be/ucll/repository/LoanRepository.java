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

    public LoanRepository(UserRepository userRepo, PublicationRepository pubRepo) {
        User user1 = userRepo.allUsers().get(0); // 21Savage
        User user2 = userRepo.allUsers().get(1); // Jane Toe

        List<Publication> publications1 = new ArrayList<>();
        publications1.add(pubRepo.getAllPublications().get(0)); // Harry Potter

        List<Publication> publications2 = new ArrayList<>();
        publications2.add(pubRepo.getAllPublications().get(2)); // Time (Magazine)

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
                        // Active = not returned AND endDate is today or in the future
                        return !loan.isReturned() && !loan.getEndDate().isBefore(LocalDate.now());
                    }
                    return true; // If not filtering, include all
                })
                .collect(Collectors.toList());
    }


    public void deleteUserLoans(String email) {
        List<Loan> allLoans = findLoanByUser(email,false);
        for(Loan loan : allLoans){
            loans.remove(loan);
        }
    }
}
