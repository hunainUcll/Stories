package be.ucll.integration;

import be.ucll.config.DbInitializer;
import be.ucll.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        classes = be.ucll.LibraryApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
class MembershipIntegrationTest {

    @Autowired WebTestClient      webTestClient;
    @Autowired DbInitializer      dbInitializer;
    @Autowired LoanRepository     loanRepository;
    @Autowired MembershipRepository membershipRepository;
    @Autowired UserRepository     userRepository;
    @Autowired ProfileRepository  profileRepository;
    @Autowired PublicationRepository publicationRepository;

    @BeforeEach
    void resetDatabase() {
        loanRepository.deleteAll();
        membershipRepository.deleteAll();
        userRepository.deleteAll();
        profileRepository.deleteAll();
        publicationRepository.deleteAll();
        dbInitializer.initialize();
    }

    /* ---------- happy path ---------- */
    @Test
    void postMembershipWithValidFreeLoansReturnsUserAndBalance() {
        String email = "birgit.doe@ucll.be";     // user without membership yet
        String body = """
        {
            "startDate": "2025-06-01",
            "endDate":   "2026-06-01",
            "type":      "SILVER",
            "freeLoans": 7
        }
        """;

        webTestClient.post()
                .uri("/users/" + email + "/membership")
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                // latest membership should be the one we just added
                .expectBody()
                .jsonPath("$.memberships[-1:].freeLoans").isEqualTo(7);
    }

    /* ---------- validation error ---------- */
    @Test
    void postMembershipWithInvalidFreeLoansGivesBadRequest() {
        String email = "birgit.doe@ucll.be";
        String body = """
        {
            "startDate": "2025-06-01",
            "endDate":   "2026-06-01",
            "type":      "BRONZE",
            "freeLoans": 8     // illegal for BRONZE
        }
        """;

        webTestClient.post()
                .uri("/users/" + email + "/membership")
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error")
                .isEqualTo("Invalid number of free loans for membership type.");
    }
}
