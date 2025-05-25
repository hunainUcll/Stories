package be.ucll.repository;

import be.ucll.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface LoanRepository extends JpaRepository<Loan,Long>{

    List<Loan> findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(String email, LocalDate now);

    List<Loan> findAllByUserEmail(String email);

    void deleteAllByUserEmail(String email);

}
