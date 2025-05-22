package be.ucll.repository;


import be.ucll.model.Loan;
import be.ucll.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findUsersByAgeAfter(int i);

    List<User> findUsersByAgeBetween(@PositiveOrZero(message = "age cannot be under 0") @Max(value = 101,message = "age cannot be over 101") int ageAfter, @PositiveOrZero(message = "age cannot be under 0") @Max(value = 101,message = "age cannot be over 101") int ageBefore);
    List<User> findUserByName(@NotBlank(message = "Name is required.") @Length(min = 4,max = 25,message = "Name must be in between 4 and 25 characters long") String name);
    List<User> findUsersByName(@NotBlank(message = "Name is required.") @Length(min = 4,max = 25,message = "Name must be in between 4 and 25 characters long") String name);
    boolean existsUserByEmail(@Email(message = "the email does not contain @ symbol." ) String email);
    User findUserByEmail(@Email(message = "the email does not contain @ symbol." ) String email);
    void deleteUserByEmail(@Email(message = "the email does not contain @ symbol." ) String email);
    User findFirstByOrderByAgeDesc();
    List<User> findByInterestIgnoreCases();
}
