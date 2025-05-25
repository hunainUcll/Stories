package be.ucll.repository;

import be.ucll.model.Membership;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface MembershipRepository extends JpaRepository<Membership,Long> {

    Membership findByUserEmailAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String email, LocalDate date1, LocalDate date2);


}
