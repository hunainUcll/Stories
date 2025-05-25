package be.ucll.service;

import be.ucll.model.Membership;
import be.ucll.model.Profile;
import be.ucll.model.User;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.ProfileRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final MembershipRepository membershipRepository;
    private final LoanService loanService;

    public UserService(UserRepository userRepository,
                       ProfileRepository profileRepository,
                       MembershipRepository membershipRepository,
                       LoanService loanService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.membershipRepository = membershipRepository;
        this.loanService = loanService;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllAdultUsers() {
        return userRepository.findByAgeGreaterThanEqual(18);
    }

    public List<User> getUsersInAgeRange(int minAge, int maxAge) {
        if (minAge > maxAge) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Minimum age cannot be greater than maximum age."
            );
        }
        return userRepository.findByAgeBetween(minAge, maxAge);
    }

    public List<User> getUsersByName(String name) {
        List<User> users = userRepository.findByNameContainingIgnoreCase(name);
        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No users found with the specified name."
            );
        }
        return users;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with email: " + email
                ));
    }

    public List<User> getUsersByInterestAndAge(String interest, int age) {
        // Validate interest
        if (interest == null || interest.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interest cannot be empty."
            );
        }

        // Validate age
        if (age < 0 || age > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid age. Age must be between 0 and 150."
            );
        }

        // Get profiles with the interest
        List<Profile> profiles = profileRepository.findByInterestsContainingIgnoreCase(interest);

        // Process users
        List<User> users = profiles.stream()
                .map(Profile::getUser)
                .filter(user -> user.getAge() > age)
                .sorted(Comparator.comparing(
                        user -> user.getProfile().getLocation(),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .collect(Collectors.toList());

        // Check if empty
        if (users.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No users found with interest in " + interest + " and older than " + age
            );
        }

        return users;
    }

    public void deleteUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User does not exist"
                ));

        // Delete user
        userRepository.delete(user);
    }

    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User already exists."
            );
        }

        return userRepository.save(user);
    }

    public User updateUser(String email, User updatedUser) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User does not exist"
                ));

        // Update fields (email cannot be changed)
        existingUser.setName(updatedUser.getName());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setAge(updatedUser.getAge());

        // Handle profile update
        if (updatedUser.getProfile() != null) {
            Profile profile = updatedUser.getProfile();
            if (existingUser.getProfile() != null) {
                // Update existing profile
                Profile existingProfile = existingUser.getProfile();
                existingProfile.setBio(profile.getBio());
                existingProfile.setLocation(profile.getLocation());
                existingProfile.setInterests(profile.getInterests());
                profileRepository.save(existingProfile);
            } else {
                // Create new profile
                profileRepository.save(profile);
                existingUser.setProfile(profile);
            }
        } else {
            // Remove profile if it exists
            if (existingUser.getProfile() != null) {
                profileRepository.delete(existingUser.getProfile());
                existingUser.setProfile(null);
            }
        }

        return userRepository.save(existingUser);
    }

    public User getOldestUser() {
        return userRepository.findTopByOrderByAgeDesc()
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No oldest user found."
                ));
    }

    public List<User> getUsersByInterest(String interest) {
        if (interest == null || interest.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Interest cannot be empty."
            );
        }

        List<Profile> profiles = profileRepository.findByInterestsContainingIgnoreCase(interest);

        if (profiles.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No users found with interest in " + interest
            );
        }

        return profiles.stream()
                .map(Profile::getUser)
                .toList();
    }

    public User addMembership(String email, Membership membership) {
        User user = getUserByEmail(email);

        // Validate dates
        LocalDate today = LocalDate.now();
        if (membership.getStartDate() == null || membership.getStartDate().isBefore(today)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start date must be equal or after today."
            );
        }

        if (!List.of("GOLD", "SILVER", "BRONZE").contains(membership.getType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid membership type."
            );
        }


        if (membership.getEndDate() == null ||
                !membership.getEndDate().equals(membership.getStartDate().plusYears(1))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must be 1 year after start date."
            );
        }

        // Check for overlaps
        List<Membership> existingMemberships = membershipRepository.findByUserEmail(email);
        for (Membership existing : existingMemberships) {
            if (membership.getStartDate().isBefore(existing.getEndDate()) &&
                    membership.getEndDate().isAfter(existing.getStartDate())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User has already a membership on that date."
                );
            }
        }

        user.addMembership(membership);

        membershipRepository.save(membership);
        return userRepository.save(user);
    }

    public Membership getMembershipByDate(String email, LocalDate date) {

        User user = userRepository.findByEmail(email)      // ← back to findByEmail
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User does not exist."));

        return membershipRepository.findActiveMembership(email, date)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No membership found for user on date " + date + "."));
    }




}
