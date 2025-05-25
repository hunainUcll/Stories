package be.ucll.repository;

import be.ucll.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByUserEmail(String email);
    @Query("""
       SELECT m FROM Membership m
       WHERE m.user.email = :email
         AND :date BETWEEN m.startDate AND m.endDate
       """)
    Optional<Membership> findActiveMembership(String email, LocalDate date);
}