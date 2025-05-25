package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    private  LoanRepository loanRepository;
    private  UserRepository userRepo;
    private PublicationRepository publicationRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepo,PublicationRepository publicationRepository) {
        this.loanRepository = loanRepository;
        this.userRepo = userRepo;
        this.publicationRepository = publicationRepository;
    }

    public List<Loan> getLoansByUser(String email, boolean onlyActive) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User not found with given email");  // Handle if user not found basically service is to check if everything is fein
        }
        if (onlyActive) {
            return loanRepository.findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email, LocalDate.now());
        } else {
            return loanRepository.findAllByUserEmail(email);
        }
    }

    public String deleteUserLoans(String email) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User does not exist");
        }
        List<Loan> activeLoans = loanRepository.findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email, LocalDate.now());
        if (!activeLoans.isEmpty()) {
            throw new RuntimeException("User has active loans.");
        }
        List<Loan> allLoans = loanRepository.findAllByUserEmail(email);
        if (allLoans.isEmpty()) {
            throw new RuntimeException("User has no loans.");
        }
         loanRepository.deleteAllByUserEmail(email);

        return "Loans of user successfully deleted.";
    }

    public Loan setLoanForUser(String email, LocalDate startDate, List<Long> publicationIds) {

        User user = userRepo.findUserByEmail(email);
        // i donot understand why this is needed beans should take care of this no ?
        if (user == null) throw new IllegalArgumentException("User not found.");

        List<Loan> userLoans = loanRepository.findAllByUserEmail(email);
        boolean hasActive = userLoans.stream().anyMatch(l -> !l.isReturned() && l.getEndDate() != null && l.getEndDate().isAfter(LocalDate.now()));
        if (hasActive) throw new IllegalArgumentException("User already has an active loan.");

        List<Publication> publications = new ArrayList<>();
        for (Long id : publicationIds) {
            Publication pub = publicationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Publication with id " + id + " not found."));
            publications.add(pub);
        }
            Loan loan = new Loan(user, publications, startDate); // internally sets endDate to start + 30
            loanRepository.save(loan);
            return loan;


    }


}
