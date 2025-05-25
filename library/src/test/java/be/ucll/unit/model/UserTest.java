package be.ucll.unit.model;

import be.ucll.model.Profile;
import be.ucll.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testValidUser() {
        User user = new User("John Doe", "password123", "john@example.com", 25);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size());
    }

    @Test
    public void testInvalidEmail() {
        User user = new User("John Doe", "password123", "invalid-email", 25);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size()); // Expect 1 violation
        assertEquals("E-mail must be a valid email format.", violations.iterator().next().getMessage());
    }

    @Test
    public void testEmailImmutability() {
        User user = new User("John Doe", "password123", "john@example.com", 25);
        assertThrows(RuntimeException.class, () -> user.setEmail("new@example.com"));
    }

    @Test
    public void givenValidProfile_whenSettingProfileForAdultUser_thenProfileIsSet() {
        User user = new User("John Doe", "password123", "john@example.com", 25);
        Profile profile = new Profile("Bio", "Location", "Interests");

        user.setProfile(profile);

        assertNotNull(user.getProfile());
        assertEquals("Bio", user.getProfile().getBio());
    }

    @Test
    public void givenProfile_whenSettingProfileForMinorUser_thenExceptionIsThrown() {
        User user = new User("John Doe", "password123", "john@example.com", 17);
        Profile profile = new Profile("Bio", "Location", "Interests");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            user.setProfile(profile);
        });

        assertEquals("User must be at least 18 years old to have a profile.", exception.getMessage());
    }

    @Test
    public void givenProfileWithMissingFields_whenValidating_thenConstraintViolationsOccur() {
        Profile profile = new Profile("", "", "");

        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(3, violations.size());

        // Check each violation message
        boolean hasBioError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Bio is required."));
        boolean hasLocationError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Location is required."));
        boolean hasInterestsError = violations.stream()
                .anyMatch(v -> v.getMessage().equals("Interests are required."));

        assertTrue(hasBioError);
        assertTrue(hasLocationError);
        assertTrue(hasInterestsError);
    }
}