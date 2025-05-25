package be.ucll.unit.service;

import be.ucll.model.Membership;
import be.ucll.model.User;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.ProfileRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Fully‑mocked test‑suite for {@link UserService} ‑ Story 34.
 * <p>
 * ‑ No in‑memory database<br>
 * ‑ All collaborators are Mockito mocks<br>
 * ‑ Every write‑method is verified with {@code verify(...)}
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /* ---------- mocks ---------- */
    @Mock private UserRepository        userRepository;
    @Mock private ProfileRepository     profileRepository;
    @Mock private MembershipRepository  membershipRepository;

    /* ---------- system under test ---------- */
    @InjectMocks private UserService userService;

    private User john;

    @BeforeEach
    void init() {
        // constructor order = (name, password, email, age)
        john = new User("John Doe", "john1234", "john.doe@ucll.be", 25);
    }

    /* ------------------------------------------------------------
     * READ scenario: getAllUsers()
     * ---------------------------------------------------------- */
    @Test
    void getAllUsersReturnsListFromRepository() {
        when(userRepository.findAll()).thenReturn(List.of(john));

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertSame(john, result.get(0));
        verify(userRepository).findAll();
    }

    /* ------------------------------------------------------------
     * ADD user – happy path
     * ---------------------------------------------------------- */
    @Test
    void addUserPersistsAndReturnsUser() {
        when(userRepository.existsByEmail(john.getEmail())).thenReturn(false);
        when(userRepository.save(john)).thenReturn(john);

        User saved = userService.addUser(john);

        assertSame(john, saved);
        verify(userRepository).existsByEmail(john.getEmail());
        verify(userRepository).save(john);
        verifyNoInteractions(profileRepository);
    }

    /* ------------------------------------------------------------
     * ADD user – duplicate email
     * ---------------------------------------------------------- */
    @Test
    void addUserWithDuplicateEmailThrows() {
        when(userRepository.existsByEmail(john.getEmail())).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> userService.addUser(john));

        verify(userRepository).existsByEmail(john.getEmail());
        verify(userRepository, never()).save(any());
    }



    /* ------------------------------------------------------------
     * UPDATE user – verify merged entity
     * ---------------------------------------------------------- */
    @Test
    void updateUserMergesChangesAndSaves() {
        when(userRepository.findByEmail(john.getEmail()))
                .thenReturn(Optional.of(john));

        User patch = new User("Johnny", "john1234", john.getEmail(), 26);
        userService.updateUser(john.getEmail(), patch);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User persisted = captor.getValue();
        assertEquals("Johnny", persisted.getName());
        assertEquals(26, persisted.getAge());
    }

    /* ------------------------------------------------------------
     * STORY 33 helper – getMembershipByDate
     * ---------------------------------------------------------- */
    @Test
    void getMembershipByDateHappyPath() {
        String email = john.getEmail();
        LocalDate today = LocalDate.now();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(john));

        Membership active = new Membership(
                today.minusDays(5), today.plusYears(1), "SILVER", 7);
        active.setUser(john);
        when(membershipRepository.findActiveMembership(email, today))
                .thenReturn(Optional.of(active));

        Membership result = userService.getMembershipByDate(email, today);

        assertEquals("SILVER", result.getType());
        verify(userRepository).findByEmail(email);
        verify(membershipRepository).findActiveMembership(email, today);
    }
}
