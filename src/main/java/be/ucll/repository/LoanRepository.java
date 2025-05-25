package be.ucll.repository;

import be.ucll.model.*;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long>{

    // this is for loans that are active + loans that arent returned
    List<Loan> findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(String email, LocalDate now);

    // this is for one loan that is active
    Loan findLoanByUserEmailAndEndDateAfter(String userEmail, LocalDate endDateAfter);

    // this is for overdue loan
    List<Loan> findAllByUserEmailAndIsReturnedFalse( String userEmail);

    List<Loan> findAllByUserEmail(String email);

    void deleteAllByUserEmail(String email);

}
