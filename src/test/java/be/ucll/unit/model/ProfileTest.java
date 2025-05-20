package be.ucll.unit.model;

import be.ucll.model.Profile;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void givenValidProfile_whenCreating_thenNoViolations() {
        Profile profile = new Profile("Teacher at UCLL", "Leuven", "Science, reading, cooking");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenNullBio_whenCreating_thenBioIsRequiredViolation() {
        Profile profile = new Profile(null, "Leuven", "Science");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Bio is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenNullLocation_whenCreating_thenLocationIsRequiredViolation() {
        Profile profile = new Profile("Teacher", null, "Science");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Location is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenNullInterests_whenCreating_thenInterestsIsRequiredViolation() {
        Profile profile = new Profile("Teacher", "Leuven", null);
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Interests are required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenEmptyBio_whenCreating_thenBioIsRequiredViolation() {
        Profile profile = new Profile("", "Leuven", "Science");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Bio is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenEmptyLocation_whenCreating_thenLocationIsRequiredViolation() {
        Profile profile = new Profile("Teacher", "", "Science");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Location is required.", violations.iterator().next().getMessage());
    }

    @Test
    void givenEmptyInterests_whenCreating_thenInterestsIsRequiredViolation() {
        Profile profile = new Profile("Teacher", "Leuven", "");
        Set<ConstraintViolation<Profile>> violations = validator.validate(profile);
        assertEquals(1, violations.size());
        assertEquals("Interests are required.", violations.iterator().next().getMessage());
    }




}
