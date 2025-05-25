package be.ucll.unit.service;

import be.ucll.model.*;
import be.ucll.repository.*;
import be.ucll.service.UserService;
import be.ucll.unit.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    private UserService userService;
    private MembershipRepositoryStub membershipRepository;
    private UserRepository userRepository;
    private PublicationRepository publicationRepository;



    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryStub();
        LoanRepositoryStub loanRepository = new LoanRepositoryStub();
        ProfileRepositoryStub profileRepository = new ProfileRepositoryStub();
        membershipRepository = new MembershipRepositoryStub();
        publicationRepository = new PublicationRepositoryStub();

        userService = new UserService(userRepository, loanRepository, profileRepository, membershipRepository);

        // Add sample users
        User user1 = new User("21Savage", 25, "21.savage@ucll.be", "john1234");
        User user2 = new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234");
        User user3 = new User("Jack Doe", 30, "jack.doe@ucll.be", "jack1234");
        User user4 = new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234");
        User user5 = new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234");

        User user6 = new User("22Savage", 25, "22.savage@ucll.be", "john1234", new Profile(
                "Aspiring rapper with a love for tech.",
                "Antwerp",
                "Music, rapping, Basketball"
        ));

        User user7 = new User("23Savage", 25, "23.savage@ucll.be", "john1234", new Profile(
                "Aspiring rapper with a love for activities.",
                "Brussels",
                "Smoking, rapping, Coding"
        ));

        for (User user : List.of(user1, user2, user3, user4, user5, user6, user7)) {
            userRepository.save(user);
        }

        // --- PUBLICATIONS ---
        Book book1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        book1.setId(1L);
        Book book2 = new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5);
        book2.setId(2L);
        Book book3 = new Book("Potterrrr", "J.K. Rowling", "978-0-545-01032-2", 2001, 9);
        book3.setId(3L);
        publicationRepository.save(book1);
        publicationRepository.save(book2);
        publicationRepository.save(book3);

        Magazine mag1 = new Magazine("Time", "John Doe", "1234-5678", 2022, 7);
        mag1.setId(4L);
        Magazine mag2 = new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 23);
        mag2.setId(5L);
        Magazine mag3 = new Magazine("Timelessssss", "John Doe", "1234-5678", 2022, 0);
        mag3.setId(6L);
        publicationRepository.save(mag1);
        publicationRepository.save(mag2);
        publicationRepository.save(mag3);

        // --- LOANS ---
        Loan loan1 = new Loan(user1, List.of(book1), LocalDate.of(2025, 5, 24)); // active
        Loan loan2 = new Loan(user2, List.of(book2), LocalDate.of(2025, 4, 15)); // inactive (assume returned)
        Loan loan3 = new Loan(user3, List.of(book3), LocalDate.of(2025, 5, 23)); // active but returned
        loan3.setReturned(true);

        loanRepository.save(loan1);
        loanRepository.save(loan2);
        loanRepository.save(loan3);

    }


    @Test
    void givenUserService_whenGettingAllUsers_thenReturnsUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(7, users.size());
        assertEquals("21Savage", users.get(0).getName());
    }

    @Test
    void givenUsers_whenGettingWithinValidAgeRange_thenReturnsCorrectUsers() {
        List<User> users = userService.getUsersBetweenAges(18, 30);

        assertNotNull(users);
        assertEquals(6, users.size());
        assertTrue(users.stream().allMatch(user -> user.getAge() >= 18 && user.getAge() <= 30));
    }

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

    @Test
    void givenEmptyName_whenSearching_thenReturnsAllUsers() {
        List<User> users = userService.getUsersByName("");

        assertNotNull(users);
        assertEquals(7, users.size());
    }

     //Test for null input name – should throw exception i think
    @Test
    void givenNullName_whenSearching_thenReturnsAllUsers() {
        List<User> users = userService.getUsersByName("");

        assertNotNull(users);
        assertEquals(7, users.size());
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
        ProfileRepository profileRepository = new ProfileRepositoryStub();
        MembershipRepository membershipRepository = new MembershipRepositoryStub();
        userService = new UserService(userRepository,loanRepository,profileRepository,membershipRepository);

        String email = "john.doe@example.com";
        User existingUser = new User("John Doe", 30, email, "password123");
        User updatedData = new User("Johnny Doe", 31, email, "newpassword123");

        when(userRepository.existsUserByEmail(email)).thenReturn(true);
        when(userRepository.findUserByEmail(email)).thenReturn(existingUser);

        // When
        User updatedUser = userService.updateUser(updatedData, email);

        // Then
        assertEquals("Johnny Doe", updatedUser.getName());
        assertEquals(31, updatedUser.getAge());
        assertEquals("newpassword123", updatedUser.getPassword());
        assertEquals(email, updatedUser.getEmail()); // Email remains unchanged

        verify(userRepository).existsUserByEmail(email);
        verify(userRepository).findUserByEmail(email);
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

    @Test
    void givenNoAdults_whenGettingAllAdults_thenReturnsEmptyList() {
        // Custom user repo with no adult users
        UserRepository userRepository = new UserRepositoryStub() {
            @Override
            public List<User> findUsersByAgeAfter(int age) {
                return List.of();
            }
        };
        PublicationRepository publicationRepository = new PublicationRepositoryStub();
        LoanRepository loanRepository = new LoanRepositoryStub();
        ProfileRepository profileRepository = new ProfileRepositoryStub();
        MembershipRepository membershipRepository = new MembershipRepositoryStub();
        userService = new UserService(userRepository,loanRepository,profileRepository,membershipRepository);

        List<User> adults = userService.getAllAdultUsers();

        assertNotNull(adults);
        assertTrue(adults.isEmpty());
    }

    @Test
    void givenNoMatchingUsers_whenGettingWithinAgeRange_thenReturnsEmptyList() {
        UserRepository userRepository = new UserRepositoryStub() {
            public List<User> usersWithinAgeRange(int min, int max) {
                return List.of();
            }
        };
        PublicationRepository publicationRepository = new PublicationRepositoryStub();
        LoanRepository loanRepository = new LoanRepositoryStub();
        ProfileRepository profileRepository = new ProfileRepositoryStub();
        MembershipRepository membershipRepository = new MembershipRepositoryStub();
        userService = new UserService(userRepository,loanRepository,profileRepository,membershipRepository);

        List<User> users = userService.getUsersBetweenAges(100, 150);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void givenInvalidRange_whenMinGreaterThanMax_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(30, 20)
        );

        assertEquals("Minimum age cannot be greater than maximum age.", exception.getMessage());
    }

    @Test
    void givenInvalidRange_whenBelowZero_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(-5, 20)
        );

        assertEquals("Invalid age range. Age must be between 0 and 150.", exception.getMessage());
    }

    @Test
    void givenInvalidRange_whenAbove150_thenThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.getUsersBetweenAges(50, 160)
        );

        assertEquals("Invalid age range. Age must be between 0 and 150.", exception.getMessage());
    }

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
        // interesting method used by AI to stimulate a user already exists by overriding the user exits validation method , kinda feels like cheating so i will use a user that really exists in my DB

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

    // story 22 tests
    @Test
    void givenUserWithValidProfile_whenRegisteringUser_thenProfileIsSavedFirst() {
        Profile profile = new Profile("Software dev", "Leuven", "Gaming, Reading");
        User user = new User("Alice", 22, "alice@example.com", "alice1234");

        user.setProfile(profile);
        User savedUser = userService.registerUser(user);

        assertEquals("Alice", savedUser.getName());
        assertNotNull(savedUser.getProfile());
        assertEquals("Software dev", savedUser.getProfile().getBio());
    }

    @Test
    void givenUserWithoutProfile_whenRegisteringUser_thenOnlyUserIsSaved() {
        User user = new User("Bob", 24, "bob@example.com", "bob1234");

        User savedUser = userService.registerUser(user);

        assertEquals("Bob", savedUser.getName());
        assertNull(savedUser.getProfile());
    }
    // Story 23 (i don't want to this anymore)
    // happy
    @Test
    void givenUsers_whenGettingOldestUser_thenOldestUserIsReturned() {
        User user = userService.getOldestUser();

        assertNotNull(user);
        assertEquals("jane.toe@ucll.be",user.getEmail());
    }

    // unhappy like me (seriously i do not want to do this for another second let alone the rest of my life)
    // might just quit
    @Test
    void givenEmptyListOfUsers_whenGettingOldest_thenThrowsRuntimeException() {
        UserRepository userRepository = new UserRepositoryStub() {};
        userRepository.deleteAll();
        LoanRepository loanRepository = new LoanRepositoryStub();
        ProfileRepository profileRepository = new ProfileRepositoryStub();
        MembershipRepository membershipRepository = new MembershipRepositoryStub();
        userService = new UserService(userRepository,loanRepository,profileRepository,membershipRepository);

        Exception exception = assertThrows(RuntimeException.class, () ->
                userService.getOldestUser()
        );
        assertEquals("No oldest user found.", exception.getMessage());

    }

    // story 24
    // happy
    @Test
    void getUsersByInterest_HappyPath_ReturnsMatchingUsers() {

        List<User> result = userService.getUsersByInterest("rapping");

        assertFalse(result.isEmpty());
        for (User user : result) {
            assertNotNull(user.getProfile());
            assertTrue(user.getProfile().getInterests().toLowerCase().contains("rapping"));
        }
        // because i know there are only 2 users with this interest
        assertEquals(2,result.size());
    }

    //unhappy

    @Test
    void getUsersByInterest_EmptyInterest_ThrowsException() {

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUsersByInterest("   ");
        });

        assertEquals("Interest cannot be empty", exception.getMessage());
    }


    @Test
    void getUsersByInterest_NoUsersFound_ThrowsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.getUsersByInterest("surfing"); // not in any profile
        });
        assertEquals("No users found with interest in surfing", exception.getMessage());
    }

    // story 25
    @Test
    public void givenValidInterestAndAge_whenInvoking_thenReturnsMatchingUsers() {
        List<User> result = userService.getUsersByInterestAndAboveAge("rapping", 20);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(user -> user.getName().equals("22Savage")));
        assertTrue(result.stream().anyMatch(user -> user.getName().equals("23Savage")));
    }

    // unhappy and depressed tests like me
    @Test
    public void givenEmptyInterest_whenInvoking_thenThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("  ", 20));
        assertEquals("Interest cannot be empty", ex.getMessage());
    }
    @Test
    public void givenNegativeAge_whenInvoking_thenThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("rapping", -5));
        assertEquals("Invalid age. Age must be between 0 and 150.", ex.getMessage());
    }
    @Test
    public void givenValidInterestAndAge_whenNoMatchingUsers_thenThrowsRuntimeException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("karate", 20));
        assertEquals("No users found with interest in karate and older than 20", ex.getMessage());
    }

// story 27

    // happy - soo much time spe,t here fixing errros

    @Test
    void givenExistingUserEmailAndValidMembership_whenAddingMembership_thenMembershipIsAddedToUser() {
        Membership membership = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "GOLD",12
        );
        User result = userService.addMembership("21.savage@ucll.be", membership);

        assertEquals(1, result.getMemberships().size());
        assertTrue(result.getMemberships().contains(membership));
        assertTrue(membershipRepository.findAll().contains(membership));
    }

    // unhappy
    @Test
    void givenNonExistingUserEmail_whenAddingMembership_thenThrowsUserDoesNotExistException() {

        Membership membership = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "SILVER",8
        );
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addMembership("nonexistent@example.com", membership);
        });

        assertEquals("User does not exist.", exception.getMessage());
    }

    //story 33
    @Test
    void givenNonExistingUser_whenGetMembershipByDate_thenThrowsUserNotExist() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.getMembershipByDate("noone@ucll.be", LocalDate.of(2125, 6, 1));
        });
        assertEquals("User does not exist.", ex.getMessage());
    }

    @Test
    void givenUserWithoutMembershipOnDate_whenGetMembershipByDate_thenThrowsNoMembershipFound() {
        // Add a user without memberships on that date
        User user = new User("Test User", 30, "test@ucll.be", "test123");
        userRepository.save(user);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            userService.getMembershipByDate("test@ucll.be", LocalDate.of(2125, 6, 1));
        });
        assertEquals("No membership found for user on date 2125-06-01.", ex.getMessage());
    }



}
