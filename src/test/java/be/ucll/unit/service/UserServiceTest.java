package be.ucll.unit.service;

import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = new UserRepository();
        PublicationRepository publicationRepository = new PublicationRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository,publicationRepository);
        userService = new UserService(userRepository,loanRepository);
    }


    // note for the teacher - used ai here to only generate the comments to make it simpler for me to understand whats going on

// ✅ HAPPY PATH TESTS

    // ✅ Test retrieving all users
    @Test
    void givenUserService_whenGettingAllUsers_thenReturnsUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(5, users.size());
        assertEquals("21Savage", users.get(0).getName());
    }

    // ✅ Test filtering users within a valid age range
    @Test
    void givenUsers_whenGettingWithinValidAgeRange_thenReturnsCorrectUsers() {
        List<User> users = userService.getUsersBetweenAges(18, 30);

        assertNotNull(users);
        assertEquals(3, users.size());
        assertTrue(users.stream().allMatch(user -> user.getAge() >= 18 && user.getAge() <= 30));
    }

    // ✅ Test getting users by name (full or partial match)
    @Test
    void givenUsersExist_whenSearchingByName_thenReturnsMatchingUsers() {
        List<User> users = userService.getUsersByName("Doe");

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("Jack Doe", users.get(0).getName());
        assertEquals("Sarah Doe", users.get(1).getName());
    }

    @Test
    void givenUsersExist_whenSearchingByPartialName_thenReturnsMatchingUsers() {
        List<User> users = userService.getUsersByName("21Sav");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("21Savage", users.get(0).getName());
    }

    // ✅ Test for empty input name – should return all users (default behavior)
    @Test
    void givenEmptyName_whenSearching_thenReturnsAllUsers() {
        List<User> users = userService.getUsersByName("");

        assertNotNull(users);
        assertEquals(5, users.size());
    }

    // ✅ Test for null input name – should also return all users (default behavior)
    @Test
    void givenNullName_whenSearching_thenReturnsAllUsers() {
        List<User> users = userService.getUsersByName(null);

        assertNotNull(users);
        assertEquals(5, users.size());
    }
    // happy test story 14 registering a user successfully
    @Test
    void givenNewUser_whenRegistering_thenAddsUser() {
        User newUser = new User("New User",59, "new@ucll.be", "pass1234");
        User result = userService.registerUser(newUser); // side note the method register user return the user from the parameter after user was successfully registered thus User result is only possible if User new user was registered successfully
        // Assert
        assertNotNull(result);
        assertEquals("new@ucll.be", result.getEmail());
    }

    // happy test Story 15

    // POTENTIAL PROBLEM ARISES HERE DO I NEED TO MAKE IT SO THAT THE TESTS test THE PROGRAM WITHOUT BEING DEPENDENT ON THE DATA FROM THE DATABASE IN TEH REPO ???????????????????????????????? IF YES I HAVE TO CHANGE EVERYTHINNGGGGGG
    // THE TEST BELOW ISN'T DEPENDENT ON THE REPO DATA
    // to make the test independent had to use mockito when and verify
    @Test
    void givenExistingUser_whenUpdatingUser_thenUserIsSuccessfullyUpdated() {
        // Given
        UserRepository userRepository = mock(UserRepository.class);
        LoanRepository loanRepository = mock(LoanRepository.class);
        UserService userService = new UserService(userRepository,loanRepository);

        String email = "john.doe@example.com";
        User existingUser = new User("John Doe", 30, email, "password123");
        User updatedData = new User("Johnny Doe", 31, email, "newpassword123");

        when(userRepository.userExists(email)).thenReturn(true);
        when(userRepository.findUsersByEmail(email)).thenReturn(existingUser);

        // When
        User updatedUser = userService.updateUser(updatedData, email);

        // Then
        assertEquals("Johnny Doe", updatedUser.getName());
        assertEquals(31, updatedUser.getAge());
        assertEquals("newpassword123", updatedUser.getPassword());
        assertEquals(email, updatedUser.getEmail()); // Email remains unchanged

        verify(userRepository).userExists(email);
        verify(userRepository).findUsersByEmail(email);
    }

    // STORY 16 happy test
    // from now I will try to make the tests independent of the current user repo data just to be safe
    // actually nvm it's too tough and too much work and too many errors
    // I will leave tomorrow's problem for tomorrows me - Saitama(one punch man)
    @Test
    void deleteUser_ShouldReturnSuccessMessage_WhenUserExistsAndHasNoActiveLoans() {
        String email = "jane.toe@ucll.be";
        String result = userService.deleteUser(email);
        assertEquals("User successfully deleted", result);
        assertFalse(userService.userExists(email));
    }






    // ❌ UNHAPPY PATH TESTS

    // ❌ No adult users in system
    @Test
    void givenNoAdults_whenGettingAllAdults_thenReturnsEmptyList() {
        // Custom user repo with no adult users
        UserRepository userRepository = new UserRepository() {
            @Override
            public List<User> usersOlderThan(int age) {
                return List.of();
            }
        };
        PublicationRepository publicationRepository = new PublicationRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository, publicationRepository);
        UserService userService = new UserService(userRepository, loanRepository);

        List<User> adults = userService.getAllAdultUsers();

        assertNotNull(adults);
        assertTrue(adults.isEmpty());
    }

    // ❌ No users in given age range
    @Test
    void givenNoMatchingUsers_whenGettingWithinAgeRange_thenReturnsEmptyList() {
        UserRepository userRepository = new UserRepository() {
            public List<User> usersWithinAgeRange(int min, int max) {
                return List.of();
            }
        };
        PublicationRepository publicationRepository = new PublicationRepository();
        LoanRepository loanRepository = new LoanRepository(userRepository, publicationRepository);
        UserService userService = new UserService(userRepository, loanRepository);

        List<User> users = userService.getUsersBetweenAges(100, 150);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    // ❌ Invalid input: min > max age
    @Test
    void givenInvalidRange_whenMinGreaterThanMax_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(30, 20)
        );

        assertEquals("Minimum age cannot be greater than maximum age.", exception.getMessage());
    }

    // ❌ Invalid input: age below 0
    @Test
    void givenInvalidRange_whenBelowZero_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(-5, 20)
        );

        assertEquals("Invalid age range. Age must be between 0 and 150.", exception.getMessage());
    }

    // ❌ Invalid input: age above 150
    @Test
    void givenInvalidRange_whenAbove150_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(50, 160)
        );

        assertEquals("Invalid age range. Age must be between 0 and 150.", exception.getMessage());
    }

    // ❌ Name not found: should throw custom exception
    @Test
    void givenNoMatchingUsers_whenSearchingByName_thenThrowsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userService.getUsersByName("Nonexistent")
        );

        assertEquals("No users found with the specified name.", exception.getMessage());
    }

    //unhappy test story 14 user already exists when registering
    @Test
    void givenExistingEmail_whenRegistering_thenThrowsException() {
        // interesting method used by AI to stimulate a user already exists by overriding the userexits validation method , kinda feels like cheating so i will use a user that really exists in my DB

        /* UserRepository userRepository = new UserRepository() {
            @Override
            public boolean userExists(String email) {
                return true; // Simulate user already exists
            }
          }; */

        User duplicateUser = new User("Existing", 32, "21.savage@ucll.be", "pass123433");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(duplicateUser);
        });
        // side note user exists only checks email (primary key)
        assertEquals("User already exists.", exception.getMessage());
    }

    //story 15 unhappy test
    @Test
    void givenNonExistentUser_whenUpdatingUser_thenThrowsUserDoesNotExistException() {
        User userToUpdate = new User("Fake User", 30, "nonexistent@example.com", "fake1uu23");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(userToUpdate, "nonexistent@example.com");
        });
        assertEquals("User does not exist", exception.getMessage());
    }

    // Story 17 unhappy tests

    @Test
    void deleteUser_ShouldThrowException_WhenUserDoesNotExist() {
        // Given
        String email = "user@example.com";
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(email));
        assertEquals("User does not exist", exception.getMessage());
        // double tap
        assertFalse(userService.userExists(email));

    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserHasActiveLoans() {
        // Given
        String email = "21.savage@ucll.be";
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(email));
        assertEquals("User has active loans.", exception.getMessage());
    }
}
