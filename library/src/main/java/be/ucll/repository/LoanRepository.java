package be.ucll.repository;

import be.ucll.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    /* --------------------------------------------------------------
     * 1  All loans for a user (eager‑fetch publications for convenience)
     * ------------------------------------------------------------ */
    @Query("""
            SELECT DISTINCT l
              FROM Loan l
              LEFT JOIN FETCH l.publications
             WHERE l.user.email = ?1
            """)
    List<Loan> findByUserEmail(String email);

    /* --------------------------------------------------------------
     * 2  Active loans on a given day – filtered by user AND not returned
     * ------------------------------------------------------------ */
    @Query("""
            SELECT DISTINCT l
              FROM Loan l
              LEFT JOIN FETCH l.publications
             WHERE l.user.email = ?1
               AND l.startDate <= ?2
               AND l.endDate   >= ?2
               AND l.returned   = false
            """)
    List<Loan> findActiveLoansByUserEmail(String email, LocalDate date);

    /* --------------------------------------------------------------
     * 3  Delete all loans of a user (used by UserService)
     * ------------------------------------------------------------ */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Loan l WHERE l.user.email = ?1")
    void deleteByUserEmail(String email);

    /* --------------------------------------------------------------
     * 4  Overdue loans – handy for Story 31/32 (late returns)
     * ------------------------------------------------------------ */
    List<Loan> findByUserEmailAndReturnedFalse(String email);
}
