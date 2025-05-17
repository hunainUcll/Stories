package be.ucll.unit.model;

import be.ucll.model.Magazine;
import be.ucll.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.Validation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MagazineTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setupValidatorFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    // Happy tests

    @Test
    void givenValidMagazineDetails_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("Time", "John Doe", "1234-5678", 2022, 7);

        assertEquals("Time", magazine.getTitle());
        assertEquals("John Doe", magazine.getEditor());
        assertEquals("1234-5678", magazine.getISSN());
        assertEquals(2022, magazine.getPublicationYear());
        assertEquals(7, magazine.getAvailableCopies());

        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenValidMagazineWithMultipleCopies_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("National Geographic", "Jane Smith", "9876-5432", 1995, 15);

        assertEquals("National Geographic", magazine.getTitle());
        assertEquals("Jane Smith", magazine.getEditor());
        assertEquals("9876-5432", magazine.getISSN());
        assertEquals(1995, magazine.getPublicationYear());
        assertEquals(15, magazine.getAvailableCopies());

        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertTrue(violations.isEmpty());
    }

    @Test
    void givenValidRecentPublicationYear_whenCreatingMagazine_thenMagazineHasExpectedValues() {
        Magazine magazine = new Magazine("Tech Monthly", "Editor Name", "1023-4567", 2024, 2);

        assertEquals("Tech Monthly", magazine.getTitle());
        assertEquals("Editor Name", magazine.getEditor());
        assertEquals("1023-4567", magazine.getISSN());
        assertEquals(2024, magazine.getPublicationYear());
        assertEquals(2, magazine.getAvailableCopies());

        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertTrue(violations.isEmpty());
    }

    // Unhappy tests

    @Test
    void givenEmptyTitle_whenValidatingMagazine_thenValidationFails() {
        Magazine magazine = new Magazine("", "John Doe", "1234-5678", 2022, 7);
        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertTrue(violations.stream().anyMatch(v ->
                v.getPropertyPath().toString().equals("title") &&
                        v.getMessage().equals("title is required.")
        ));
    }

    @Test
    void givenNullTitle_whenValidatingMagazine_thenValidationFails() {
        Magazine magazine = new Magazine(null, "John Doe", "1234-5678", 2022, 7);
        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertEquals(1, violations.size());
        ConstraintViolation<Magazine> violation = violations.iterator().next();
        assertEquals("title is required.", violation.getMessage());

    }

    @Test
    void givenEmptyEditor_whenCreatingMagazine_thenThrowsRuntimeException() {
        Magazine magazine =  new Magazine("Time", "", "1234-5678", 2022, 7);
        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertEquals(1, violations.size());
        ConstraintViolation<Magazine> violation = violations.iterator().next();
        assertEquals("editor is required.", violation.getMessage());
    }

    @Test
    void givenNullEditor_whenCreatingMagazine_thenThrowsRuntimeException() {
        Magazine magazine = new Magazine("Time", null, "1234-5678", 2022, 7);
        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertEquals(1, violations.size());
        ConstraintViolation<Magazine> violation = violations.iterator().next();
        assertEquals("editor is required.", violation.getMessage());
    }

    @Test
    void givenWrongISSNFormat_whenCreatingMagazine_thenThrowsRuntimeException() {
        Exception exception = assertThrows(RuntimeException.class, () ->
                new Magazine("Time", "John Doe", "12345678", 2022, 7));
        assertEquals("ISSN has wrong format", exception.getMessage());
    }

    @Test
    void givenFuturePublicationYear_whenCreatingMagazine_thenThrowsRuntimeException() {
        int nextYear = java.time.Year.now().getValue() + 1;
        Exception exception = assertThrows(RuntimeException.class, () -> new Magazine("Time", "John Doe", "1234-5678", nextYear, 7));
        assertEquals("Publication year cannot be in the future.", exception.getMessage());
    }

    @Test
    void givenNegativeCopies_whenValidatingMagazine_thenValidationFails() {
        Magazine magazine = new Magazine("Time", "John Doe", "1234-5678", 2022, -3);
        Set<ConstraintViolation<Magazine>> violations = validator.validate(magazine);
        assertEquals(1, violations.size());
        ConstraintViolation<Magazine> violation = violations.iterator().next();
        assertEquals("Available copies must not be a negative number.", violation.getMessage());
    }
}
