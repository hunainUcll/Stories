package be.ucll.unit.model;

import be.ucll.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {

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
    void givenEmptyName_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> { new User("", 25, "user@example.com", "password123");});
        assertEquals("Name is required.",exception.getMessage());
    }

    @Test
    void givenNullName_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {new User(null,24,"yser@exwampk.com","passsword1234");});
        assertEquals("Name is required.",exception.getMessage());
    }

    @Test
    void givenAgeZero_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, ()-> {new User("harrold",0,"user@example.com","password12345");});
        assertEquals("Invalid age",exception.getMessage());
    }

    @Test
    void givenNegativeAge_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, ()-> {new User("harrold",-10,"harrold@example.com","password1234");});
        assertEquals("Invalid age",exception.getMessage());
    }

    @Test
    void givenAgeOver101_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,()->{new User("harrold",102,"harold@example.com","password1234");});
        assertEquals("Invalid age",exception.getMessage());
    }

    @Test
    void givenEmailWithoutAtSymbol_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,()->{new User("harrold",23,"harroldatexample.com","pasword123344");});
        assertEquals("The email is invalid",exception.getMessage());
    }

    @Test
    void givenInvalidEmail_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class,()->{ new User("harrold", 24,"invalidemail","Password1234");});
        assertEquals("The email is invalid",exception.getMessage());
    }

    @Test
    void givenEmailWithoutDot_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {new User("John", 30, "invalid@emailcom", "password123");});
        assertEquals("The email is invalid", exception.getMessage());
    }

    @Test
    void givenTooShortPassword_whenCreatingUser_thenThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {new User("John", 30, "john@example.com", "pass");});
        assertEquals("Password must be at least 8 characters long.", exception.getMessage());
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