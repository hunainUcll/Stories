package be.ucll.integration;

import be.ucll.model.User;
import be.ucll.config.DbInitializer;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.MembershipRepository;
import be.ucll.repository.UserRepository;
import be.ucll.repository.ProfileRepository;
import be.ucll.repository.PublicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(
        classes = be.ucll.LibraryApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
public class UserIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DbInitializer dbInitializer;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @BeforeEach
    public void setUp() {
        // Clear dependent entities first
        loanRepository.deleteAll();        // Deletes loans first
        membershipRepository.deleteAll();
        userRepository.deleteAll();
        profileRepository.deleteAll();
        publicationRepository.deleteAll();

        // Then re-initialize
        dbInitializer.initialize();
    }

    @Test
    public void givenUsers_whenGetAllUsers_thenAllUsersReturned() {
        webTestClient.get().uri("/users")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(5); // Now expects 5 users
    }

    @Test
    public void givenExistingUser_whenDeleteUser_thenUserIsDeleted() {
        String email = "john.doe@ucll.be";

        // Check user exists before deletion
        webTestClient.get().uri("/users/" + email)
                .exchange()
                .expectStatus().isOk();

        // Delete user
        webTestClient.delete().uri("/users/" + email)
                .exchange()
                .expectStatus().isOk();

        // Verify deletion
        webTestClient.get().uri("/users/" + email)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void givenUserWithProfile_whenPostUser_thenUserWithProfileIsReturned() {
        String userJson = """
        {
            "name": "Test User",
            "email": "test@example.com",
            "password": "test1234",
            "age": 20,
            "profile": {
                "bio": "Test Bio",
                "location": "Test Location",
                "interests": "Test Interests"
            }
        }
        """;

        webTestClient
                .post().uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test User")
                .jsonPath("$.profile.bio").isEqualTo("Test Bio")
                .jsonPath("$.profile.location").isEqualTo("Test Location")
                .jsonPath("$.profile.interests").isEqualTo("Test Interests");
    }

    @Test
    public void givenUserWithProfile_whenGetUser_thenProfileIsIncluded() {
        webTestClient
                .get().uri("/users/john.doe@ucll.be") // Directly access the user by email
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.profile.bio").isEqualTo("Teacher at UCLL")
                .jsonPath("$.profile.location").isEqualTo("Leuven")
                .jsonPath("$.profile.interests").isEqualTo("Science, reading, cooking, movies");
    }

    @Test
    public void givenMinorUserWithProfile_whenPostUser_thenErrorIsReturned() {
        String userJson = """
        {
            "name": "Minor User",
            "email": "minor@example.com",
            "password": "minor1234",
            "age": 17,
            "profile": {
                "bio": "Test Bio",
                "location": "Test Location",
                "interests": "Test Interests"
            }
        }
        """;

        webTestClient
                .post().uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue(userJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("User must be at least 18 years old to have a profile.");
    }

    @Test
    public void whenGetOldestUser_thenReturnOldestUserFromTestData() {
        webTestClient.get().uri("/users/oldest")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Jane Toe")
                .jsonPath("$.age").isEqualTo(30);
    }

    @Test
    public void whenGetOldestUserWithEmptyDatabase_thenReturnError() {
        // Clear database for this specific test
        userRepository.deleteAll();

        webTestClient.get().uri("/users/oldest")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No oldest user found.");
    }

    @Test
    public void givenUsersWithInterest_whenGetByInterest_thenUsersReturned() {
        webTestClient.get().uri("/users/interest/music")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(User.class).hasSize(1);
    }

    @Test
    public void givenEmptyInterest_whenGetByInterest_thenBadRequest() {
        webTestClient.get().uri("/users/interest/  ")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Interest cannot be empty.");
    }

    @Test
    public void givenNoMatchingInterest_whenGetByInterest_thenNotFound() {
        webTestClient.get().uri("/users/interest/chess")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No users found with interest in chess");
    }

    @Test
    public void givenValidInterestAndAge_whenGetUsersByInterestAndAge_thenUsersReturnedSorted() {
        webTestClient.get().uri("/users/interest/Science/20")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Jane Toe")
                .jsonPath("$[0].profile.location").isEqualTo("Brussels")
                .jsonPath("$[1].name").isEqualTo("John Doe")
                .jsonPath("$[1].profile.location").isEqualTo("Leuven");
    }

    @Test
    public void givenEmptyInterest_whenGetUsersByInterestAndAge_thenBadRequest() {
        webTestClient.get().uri("/users/interest/  /20")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Interest cannot be empty.");
    }

    @Test
    public void givenInvalidAge_whenGetUsersByInterestAndAge_thenBadRequest() {
        webTestClient.get().uri("/users/interest/Science/200")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid age. Age must be between 0 and 150.");
    }

    @Test
    public void givenNoMatchingUsers_whenGetUsersByInterestAndAge_thenNotFound() {
        webTestClient.get().uri("/users/interest/Coding/20")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("No users found with interest in Coding and older than 20");
    }

    @Test
    public void givenValidMembership_whenPostMembership_thenMembershipIsAdded() {
        String email = "john.doe@ucll.be";

        // Get existing membership's end date
        LocalDate existingEndDate = LocalDate.now().plusDays(1).plusYears(1);
        // Set new start date to one day after existing ends
        LocalDate startDate = existingEndDate.plusDays(1);
        LocalDate endDate = startDate.plusYears(1);

        String requestBody = String.format("""
    {
        "startDate": "%s",
        "endDate": "%s",
        "type": "BRONZE"
    }
    """, startDate, endDate);

        webTestClient.post()
                .uri("/users/" + email + "/membership")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.memberships[1].type").isEqualTo("BRONZE"); // Now the second membership
    }

    @Test
    public void givenInvalidMembership_whenPostMembership_thenErrorReturned() {
        String email = "john.doe@ucll.be";
        String requestBody = """
        {
            "startDate": "2023-01-01",
            "endDate": "2024-01-02",
            "type": "PLATINUM"
        }
        """;

        webTestClient.post()
                .uri("/users/" + email + "/membership")
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest() // Changed from isOk() to isBadRequest()
                .expectBody()
                .jsonPath("$.error").value(matchesPattern(".*(Start date|End date|Invalid membership type).*"));
    }

    @Test
    void postLoanWithValidDataReturnsLoanJson() {
        String email = "birgit.doe@ucll.be";          // user with no active loan
        Long pubId = publicationRepository.findAll().get(0).getId();

        webTestClient.post()
                .uri("/users/" + email + "/loans/" + LocalDate.now())
                .header("Content-Type", "application/json")
                .bodyValue("{\"publicationIds\":[" + pubId + "]}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user.email").isEqualTo(email)
                .jsonPath("$.publications[0].id").isEqualTo(pubId);
    }

    @Test
    void postLoanWhenUserHasActiveLoanReturnsBadRequest() {
        String email = "jane.toe@ucll.be";            // seeded with active loan
        Long pubId = publicationRepository.findAll().get(0).getId();

        webTestClient.post()
                .uri("/users/" + email + "/loans/" + LocalDate.now())
                .header("Content-Type", "application/json")
                .bodyValue("{\"publicationIds\":[" + pubId + "]}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("User already has an active loan.");
    }

    @Test
    void putReturnLoanHappyPath() {
        String email = "birgit.doe@ucll.be";
        LocalDate start = LocalDate.now().minusDays(2);
        Long pubId = publicationRepository.findAll().get(0).getId();

        // seed an active loan
        webTestClient.post()
                .uri("/users/" + email + "/loans/" + start)
                        .header("Content-Type", "application/json")
                                .bodyValue("{\"publicationIds\":[" + pubId + "]}")
                                        .exchange()
                                        .expectStatus().isOk();

        // return it
        webTestClient.put()
                .uri("/users/" + email + "/loans/return/" + LocalDate.now())
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.returned").isEqualTo(true)
                                .jsonPath("$.price").isEqualTo( "2.0" );           // 1 pub × 2 days × €1
    }

    @Test
    void getMembershipOnDateReturnsMembershipJson() {
        String email = "jane.toe@ucll.be";
        LocalDate today = LocalDate.now();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/" + email + "/membership")
                        .queryParam("date", today)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.type").isEqualTo("SILVER");
    }

    @Test
    void getMembershipOnDateWhenNoneExistsReturns404() {
        String email = "birgit.doe@ucll.be";
        LocalDate today = LocalDate.now();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/" + email + "/membership")
                        .queryParam("date", today)
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("No membership found for user on date " + today + ".");
    }


}