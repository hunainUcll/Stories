package be.ucll.unit.model;

import be.ucll.model.User;
import jakarta.validation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator(){
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    @AfterAll
    public static void close(){
        validatorFactory.close();
    }


    @Test
    public void givenValidValues_whenCreatingUser_thenUserIsCreatedWithThoseValues() {
        User user = new User("John Doe", 56, "john.doe@ucll.be", "john1234");
        assertEquals("John Doe", user.getName());
        assertEquals(56, user.getAge());
        assertEquals("john.doe@ucll.be", user.getEmail());
        assertEquals("john1234", user.getPassword());
    }



    // unhappy(like me) tests below
    @Test
    void givenEmptyName_whenCreatingUser_thenNameLengthViolationIsThrown() {
        User user = new User("     ", 25, "user@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        // triggers 2 violations because name is blank and less than 4 characters
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Name is required.", violation.getMessage());
    }

    @Test
    void givenNullName_whenCreatingUser_thenNameNotNullViolationIsThrown() {
        User user = new User(null, 24, "user@example.com", "password123");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Name is required.", violation.getMessage());
    }


    @Test
    void givenNegativeAge_whenCreatingUser_thenAgeMinViolationIsThrown() {
        User user = new User("Harrold", -10, "user@example.com", "password12345");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("age cannot be under 0", violation.getMessage());
    }

    @Test
    void givenAgeOver101_whenCreatingUser_thenAgeMaxViolationIsThrown() {
        User user = new User("Harrold", 102, "user@example.com", "password12345");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("age cannot be over 101", violation.getMessage());
    }

    @Test
    void givenEmailWithoutAtSymbol_whenCreatingUser_thenEmailFormatViolationIsThrown() {
        User user = new User("Harrold", 23, "harroldatexample.com", "password12345");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("the email does not contain @ symbol.", violation.getMessage());
    }

    @Test
    void givenEmailWithoutDotSymbol_whenCreatingUser_thenEmailFormatViolationIsThrown() {
        User user = new User("Harrold", 24, "invalidemailgoogle.om", "password12345");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("the email does not contain @ symbol.", violation.getMessage());
    }

    // the test below fails and i donot know why ???????????
    // apparently @Email annotation does not detect dots (".")
    // thus using setter input validation
    @Test
    void givenEmailWithoutDot_whenCreatingUser_thenEmailFormatViolationIsThrown() {
        Exception exception = assertThrows(RuntimeException.class,()->{ new User("harrold", 24,"invalidemail","Password1234");});
        assertEquals("Email must contain . symbol.",exception.getMessage());
    }

    @Test
    void givenTooShortPassword_whenCreatingUser_thenPasswordMinViolationIsThrown() {
        User user = new User("John", 30, "john@example.com", "pass");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Password must be at least 8 characters long.", violation.getMessage());
    }
    // neat trick here did not know about repeat function
    @Test
    void givenTooLongPassword_whenCreatingUser_thenPasswordMinViolationIsThrown() {
        User user = new User("John", 30, "john@example.com", "a".repeat(26));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Password must not be more than 25 characters long.", violation.getMessage());
    }

    @Test
    void givenTooLongName_whenCreatingUser_thenValidationFails(){
        User user = new User("a".repeat(26), 25, "jane@example.com","pass123456");
        Set<ConstraintViolation<User>> violations = validator.validate((user));
        assertEquals(1,violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("Name must be in between 4 and 25 characters long",violation.getMessage());
    }




    // story 15 update user tests
    @Test
    void givenExistingUser_whenUpdatingWithDifferentEmail_thenThrowsEmailCannotBeChangedException() {
        // Given
        User user = new User("John Doe", 25, "john@example.com", "pass12333");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            user.updateUser("John Doe",33 , "new.email@example.com", "pass1234");
        });

        assertEquals("Email cannot be changed.", exception.getMessage());
    }


}