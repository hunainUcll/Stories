package be.ucll.unit.model;

import be.ucll.model.Membership;
import be.ucll.model.User;
import jakarta.validation.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class MembershipTest {

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

    // very happy tests
    @Test
    void givenValidMembership_whenCreated_thenNoViolations() {
        User user = new User("Test User", 25, "test@ucll.be", "password123");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);
        Membership membership = new Membership(startDate, endDate, "GOLD",12);
        membership.setUser(user);

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertTrue(violations.isEmpty());
    }
    @Test
    void givenValidMembership_thenGettersReturnCorrectValues() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusYears(1);
        Membership membership = new Membership(start, end, "BRONZE",3);
        assertEquals(start, membership.getStartDate());
        assertEquals(end, membership.getEndDate());
        assertEquals("BRONZE", membership.getMembershipType());
    }


// unhappy
    @Test
    void givenPastStartDate_whenCreatingMembership_thenStartDateViolationThrown() {
        User user = new User("Test User", 25, "test@ucll.be", "password123");
        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = startDate.plusYears(1);
        Membership membership = new Membership(startDate, endDate, "SILVER",7);

        membership.setUser(user);

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertEquals(1,violations.size());
        assertEquals("Start date must be equal or after today.", violations.iterator().next().getMessage());
    }

    @Test
    void givenInvalidEndDate_whenCreatingMembership_thenIllegalArgumentThrown() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(6);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            new Membership(startDate, endDate, "BRONZE",2);
        });

        assertEquals("End date must be 1 year after the start date.", ex.getMessage());
    }

    @Test
    void givenInvalidMembershipType_whenCreatingMembership_thenPatternViolationThrown() {
        User user = new User("Test User", 25, "test@ucll.be", "password123");
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        Membership membership = new Membership(startDate, endDate, "copper",3);
        membership.setUser(user);

        Set<ConstraintViolation<Membership>> violations = validator.validate(membership);
        assertFalse(violations.isEmpty());
        assertEquals("Invalid membership type.", violations.iterator().next().getMessage());
    }



}
