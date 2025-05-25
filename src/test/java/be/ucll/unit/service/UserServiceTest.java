package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.ProfileRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private LoanRepository loanRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private MembershipRepository membershipRepository;

    @InjectMocks private UserService userService;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    @BeforeEach
    void setup() {
        user1 = new User("21Savage", 25, "21.savage@ucll.be", "john1234");
        user2 = new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234");
        user3 = new User("Jack Doe", 30, "jack.doe@ucll.be", "jack1234");
        user4 = new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234");
        user5 = new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234");
    }

    @Test
    void givenUsersInRepository_whenGetAllUsers_thenReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5));

        List<User> result = userService.getAllUsers();

        assertEquals(5, result.size());
        assertSame(user1, result.get(0));
        verify(userRepository).findAll();
    }

    @Test
    void givenUsers_whenGetUsersBetweenAges_thenReturnFilteredUsers() {
        int minAge = 18, maxAge = 30;
        List<User> filteredUsers = List.of(user1, user2, user3, user5);

        when(userRepository.findUsersByAgeBetween(minAge, maxAge)).thenReturn(filteredUsers);

        List<User> users = userService.getUsersBetweenAges(minAge, maxAge);

        assertEquals(4, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() >= minAge && u.getAge() <= maxAge));
        verify(userRepository).findUsersByAgeBetween(minAge, maxAge);
    }

    @Test
    void givenUsers_whenGetUsersByName_thenReturnMatchingUsers() {
        String search = "Doe";
        List<User> matches = List.of(user3, user4, user5);

        when(userRepository.findUsersByName(search)).thenReturn(matches);

        List<User> result = userService.getUsersByName(search);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(u -> u.getName().contains("Doe")));
        verify(userRepository).findUsersByName(search);
    }

    @Test
    void givenEmptyName_whenGetUsersByName_thenReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5));

        List<User> result = userService.getUsersByName("");

        assertEquals(5, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void givenNewUser_whenRegisterUser_thenUserIsAdded() {
        User newUser = new User("New User", 59, "new@ucll.be", "pass1234");

        when(userRepository.existsUserByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.registerUser(newUser);

        assertNotNull(result);
        assertEquals(newUser.getEmail(), result.getEmail());
        verify(userRepository).existsUserByEmail(newUser.getEmail());
        verify(userRepository).save(newUser);
    }

    @Test
    void givenExistingUser_whenUpdateUser_thenUserIsUpdated() {
        String email = "john.doe@example.com";
        User existingUser = new User("John Doe", 30, email, "password123");
        User updateData = new User("Johnny Doe", 31, email, "newpassword123");

        when(userRepository.existsUserByEmail(email)).thenReturn(true);
        when(userRepository.findUserByEmail(email)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User updatedUser = userService.updateUser(updateData, email);

        assertEquals(updateData.getName(), updatedUser.getName());
        assertEquals(updateData.getAge(), updatedUser.getAge());
        assertEquals(updateData.getPassword(), updatedUser.getPassword());
        assertEquals(email, updatedUser.getEmail());

        verify(userRepository).existsUserByEmail(email);
        verify(userRepository).findUserByEmail(email);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void givenNoAdultUsers_whenGetAllAdultUsers_thenReturnEmptyList() {
        when(userRepository.findUsersByAgeAfter(17)).thenReturn(List.of());

        List<User> adults = userService.getAllAdultUsers();

        assertNotNull(adults);
        assertTrue(adults.isEmpty());
        verify(userRepository).findUsersByAgeAfter(17);
    }

    @Test
    void givenNoUsers_whenGetUsersBetweenAges_thenReturnEmptyList() {
        when(userRepository.findUsersByAgeBetween(100, 150)).thenReturn(List.of());

        List<User> users = userService.getUsersBetweenAges(100, 150);

        assertNotNull(users);
        assertTrue(users.isEmpty());
        verify(userRepository).findUsersByAgeBetween(100, 150);
    }

    @Test
    void givenInvalidAgeRange_whenGetUsersBetweenAges_thenThrowIllegalArgumentException() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsersBetweenAges(30, 20));
        assertEquals("Minimum age cannot be greater than maximum age.", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsersBetweenAges(-5, 20));
        assertEquals("Invalid age range. Age must be between 0 and 150.", ex2.getMessage());

        IllegalArgumentException ex3 = assertThrows(IllegalArgumentException.class,
                () -> userService.getUsersBetweenAges(20, 200));
        assertEquals("Invalid age range. Age must be between 0 and 150.", ex3.getMessage());
    }




    @Test
    void givenUserNotFound_whenDeleteUser_thenThrowResponseStatusException() {
        String email = "notfound@ucll.be";

        when(userRepository.existsUserByEmail(email)).thenReturn(false);

        Exception ex = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(email));

        verify(userRepository).existsUserByEmail(email);
    }

    @Test
    void givenUserHasActiveLoans_whenDeleteUser_thenThrowRuntimeException() {
        String email = "active@ucll.be";

        when(userRepository.existsUserByEmail(email)).thenReturn(true);

        User activeUser = new User("Active User", 40, email, "active123");
        Book activeBook = new Book("Active Book", "Author", "123-4567890123", 2024, 1);
        Loan activeLoan = new Loan(activeUser, List.of(activeBook), LocalDate.now().plusDays(10));

        when(loanRepository.findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email, LocalDate.now()))
                .thenReturn(List.of(activeLoan));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(email));

        assertEquals("User has active loans.", ex.getMessage());
        verify(userRepository).existsUserByEmail(email);
        verify(loanRepository).findAllByUserEmailAndIsReturnedFalseAndEndDateAfter(email, LocalDate.now());
    }

    @Test
    void givenUserService_whenGettingAllUsers_thenReturnsUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(5, users.size());
        assertEquals("21Savage", users.get(0).getName());
        verify(userRepository).findAll();
    }

    @Test
    void givenUsers_whenGettingWithinValidAgeRange_thenReturnsCorrectUsers() {
        List<User> expected = List.of(user1, user2, user3, user5);
        when(userRepository.findUsersByAgeBetween(18, 30)).thenReturn(expected);

        List<User> users = userService.getUsersBetweenAges(18, 30);

        assertNotNull(users);
        assertEquals(4, users.size());
        assertTrue(users.stream().allMatch(u -> u.getAge() >= 18 && u.getAge() <= 30));
        verify(userRepository).findUsersByAgeBetween(18, 30);
    }

    @Test
    void givenUsersExist_whenSearchingByName_thenReturnsMatchingUsers() {
        when(userRepository.findUsersByName("Doe")).thenReturn(List.of(user3, user4, user5));

        List<User> users = userService.getUsersByName("Doe");

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("Jack Doe", users.get(0).getName());
        assertEquals("Sarah Doe", users.get(1).getName());
        verify(userRepository).findUsersByName("Doe");
    }

    @Test
    void givenUsersExist_whenSearchingByPartialName_thenReturnsMatchingUsers() {
        when(userRepository.findUsersByName("21Sav")).thenReturn(List.of(user1));

        List<User> users = userService.getUsersByName("21Sav");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("21Savage", users.get(0).getName());
        verify(userRepository).findUsersByName("21Sav");
    }

    @Test
    void givenEmptyName_whenSearching_thenReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5));

        List<User> users = userService.getUsersByName("");

        assertNotNull(users);
        assertEquals(5, users.size());
        verify(userRepository).findAll();
    }


    @Test
    void givenNullName_whenSearching_thenReturnsAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2, user3, user4, user5));

        List<User> users = userService.getUsersByName(null);

        assertNotNull(users);
        assertEquals(5, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void givenNullName_whenSearching_thenThrowsException() {
        Exception ex = assertThrows(RuntimeException.class, () -> userService.getUsersByName(null));
        assertEquals("No users found with the specified name.", ex.getMessage());
    }

    @Test
    void givenNewUser_whenRegistering_thenAddsUser() {
        User newUser = new User("New User", 59, "new@ucll.be", "pass1234");

        when(userRepository.existsUserByEmail(newUser.getEmail())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.registerUser(newUser);

        assertNotNull(result);
        assertEquals("new@ucll.be", result.getEmail());
        verify(userRepository).existsUserByEmail("new@ucll.be");
        verify(userRepository).save(newUser);
    }



    //Story 25
    @Test
    void whenInterestIsBlank_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("   ", 20));

        assertEquals("Interest cannot be empty", ex.getMessage());
    }

    @Test
    void whenAgeIsNegative_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("rapping", -5));

        assertEquals("Invalid age. Age must be between 0 and 150.", ex.getMessage());
    }

    @Test
    void whenNoUsersMatchCriteria_thenThrowsException() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUsersByInterestAndAboveAge("karate", 20));

        assertEquals("No users found with interest in karate and older than 20", ex.getMessage());
    }

    // 27

    @Test
    void whenUserEmailNotFound_thenThrowsException() {
        Membership membership = new Membership(
                LocalDate.now(),
                LocalDate.now().plusYears(1),
                "SILVER", 8
        );

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.addMembership("nonexistent@example.com", membership));

        assertEquals("User does not exist.", ex.getMessage());
    }


    //33

    // Unhappy Paths
    @Test
    void whenUserEmailNotFound_thenThrowsExceptionForMembershipByDate() {
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getMembershipByDate("noone@ucll.be", LocalDate.of(2125, 6, 1)));

        assertEquals("User does not exist.", ex.getMessage());
    }

    @Test
    void whenUserHasNoMembershipOnGivenDate_thenThrowsException() {
        User user = new User("Test User", 30, "test@ucll.be", "test123");
        userRepository.save(user);
        when(userRepository.findUserByEmail(user.getEmail())).thenReturn(user);


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getMembershipByDate("test@ucll.be", LocalDate.of(2125, 6, 1)));

        assertEquals("No membership found for user on date 2125-06-01.", ex.getMessage());
    }








}
