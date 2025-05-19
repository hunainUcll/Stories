package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoanService {
    private final LoanRepository loanRepository;
    private final UserRepository userRepo;

    public LoanService(LoanRepository loanRepository, UserRepository userRepo) {
        this.loanRepository = loanRepository;
        this.userRepo = userRepo;
    }

    public List<Loan> getLoansByUser(String email, boolean onlyActive) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User not found with given email");  // Handle if user not found basically service is to check if everything is fein
        }
        // Fetch loans by user and filter based on active status
        return loanRepository.findLoanByUser(email,onlyActive);
    }

    public String deleteUserLoans(String email) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User does not exist");
        }
        List<Loan> activeLoans = loanRepository.findLoanByUser(email, true);
        if (!activeLoans.isEmpty()) {
            throw new RuntimeException("User has active loans.");
        }
        List<Loan> allLoans = loanRepository.findLoanByUser(email, false);
        if (allLoans.isEmpty()) {
            throw new RuntimeException("User has no loans.");
        }
         loanRepository.deleteUserLoans(email);

        return "Loans of user successfully deleted.";
    }
}
