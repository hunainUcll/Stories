package be.ucll.repository;

import be.ucll.model.Membership;
import be.ucll.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query methods
    List<User> findByAgeGreaterThanEqual(int age);
    List<User> findByAgeBetween(int minAge, int maxAge);
    List<User> findByNameContainingIgnoreCase(String name);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findTopByOrderByAgeDesc();

}




