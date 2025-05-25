package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {
    private final MembershipRepository membershipRepository;
    private  LoanRepository loanRepository;
    private  UserRepository userRepo;
    private PublicationRepository publicationRepository;

    public LoanService(LoanRepository loanRepository, UserRepository userRepo, PublicationRepository publicationRepository, MembershipRepository membershipRepository) {
        this.loanRepository = loanRepository;
        this.userRepo = userRepo;
        this.publicationRepository = publicationRepository;
        this.membershipRepository = membershipRepository;
    }

    public List<Loan> getLoansByUser(String email, boolean onlyActive) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User not found with given email");  // Handle if user not found basically service is to check if everything is fein
        }
        if (onlyActive) {
            return loanRepository.findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email,LocalDate.now());
        } else {
            return loanRepository.findAllByUserEmail(email);
        }
    }

    public String deleteUserLoans(String email) {
        if (!userRepo.existsUserByEmail(email)) {
            throw new RuntimeException("User does not exist");
        }
        List<Loan> activeLoans = loanRepository.findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email,LocalDate.now());
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


    public Loan returnLoansForUser(String email, LocalDate returnDate) {
        User user = userRepo. findUserByEmail(email);
             if(user==null){throw new RuntimeException("User does not exist.");}

        Loan activeLoan = loanRepository.findLoanByUserEmailAndEndDateAfter(email,LocalDate.now());


        if(activeLoan == null){throw new RuntimeException("User has no active loan");}

        if(activeLoan.isReturned()){
            throw new RuntimeException("Loan has already been returned.");
        }

        if (returnDate == null || returnDate.isBefore(activeLoan.getStartDate())) {
            throw new RuntimeException("Invalid return date. Return date must be after start date.");
        }

        // and here you can observe useless manual labour .......
        /**if (returnDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("Return date cannot be in the future.");
        }**/

        long days = ChronoUnit.DAYS.between(activeLoan.getStartDate(), returnDate);
        if (days == 0) days = 1; // Charge minimum 1 day

        double pricePerDay = 0;
        Membership membership = user.getActiveMembershipOnDate(returnDate);

        if (membership != null && membership.getFreeLoans() > 0) {
            membership.setFreeLoans(membership.getFreeLoans() - 1);
            pricePerDay = 0;
        } else if (membership != null) {
            String membershipType = membership.getMembershipType();
                if(membershipType.contains("GOLD")){
                pricePerDay = 0.25;
                } else if(membershipType.contains("SILVER")) {
                    pricePerDay = 0.5;
                } else if (membershipType.contains("BRONZE")) {
                    pricePerDay = 0.75;
                }
        } else {
            pricePerDay = 1;
        }

        int numberOfPublications = activeLoan.getPublications().size();
        double basePrice = numberOfPublications * days * pricePerDay;


        double lateFine = 0;
        if (returnDate.isAfter(activeLoan.getEndDate())) {
            long daysLate = ChronoUnit.DAYS.between(activeLoan.getEndDate(), returnDate);
            lateFine = daysLate * 0.50 * numberOfPublications;
        }

        double totalPrice = basePrice + lateFine;

        activeLoan.setReturned(true);
        activeLoan.setEndDate(returnDate);
        activeLoan.setPrice(totalPrice);

        for (Publication pub : activeLoan.getPublications()) {
            pub.setAvailableCopies(pub.getAvailableCopies() + 1);
            publicationRepository.save(pub);
        }

        if (membership != null) {
            membershipRepository.save(membership);
        }
        return loanRepository.save(activeLoan);
        
    }
}
