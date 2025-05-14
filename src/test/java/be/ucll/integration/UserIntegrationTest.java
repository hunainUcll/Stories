package be.ucll.integration;

import be.ucll.LibraryApplication;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    public void resetData() {
        userRepository.resetRepositoryData(); // Resets the in-memory list
    }

    @Test
    public void givenUsers_whenInvokingGetUsers_thenAllUsersAreReturned() {
        client.get()
                .uri("/users")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{\"name\":\"21Savage\",\"age\":25,\"email\":\"21.savage@ucll.be\",\"password\":\"john1234\"}," +
                        "{\"name\":\"Jane Toe\",\"age\":30,\"email\":\"jane.toe@ucll.be\",\"password\":\"jane1234\"}," +
                        "{\"name\":\"Jack Doe\",\"age\":5,\"email\":\"jack.doe@ucll.be\",\"password\":\"jack1234\"}," +
                        "{\"name\":\"Sarah Doe\",\"age\":4,\"email\":\"sarah.doe@ucll.be\",\"password\":\"sarah1234\"}," +
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}" +
                        "]");
    }

    @Test
    public void givenUsers_whenInvokingGetAdults_thenOnlyAdultsAreReturned() {
        client.get()
                .uri("/users/adults")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{\"name\":\"21Savage\",\"age\":25,\"email\":\"21.savage@ucll.be\",\"password\":\"john1234\"}," +
                        "{\"name\":\"Jane Toe\",\"age\":30,\"email\":\"jane.toe@ucll.be\",\"password\":\"jane1234\"}," +
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}" +
                        "]");
    }

    @Test
    public void givenUsers_whenInvokingGetUsersByAgeRange_thenCorrectUsersReturned() {
        client.get()
                .uri("/users/age/5/25")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{\"name\":\"21Savage\",\"age\":25,\"email\":\"21.savage@ucll.be\",\"password\":\"john1234\"}," +
                        "{\"name\":\"Jack Doe\",\"age\":5,\"email\":\"jack.doe@ucll.be\",\"password\":\"jack1234\"}," +
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}" +
                        "]");
    }

    @Test
    public void givenUser_whenInvokingPost_thenUserIsSaved() {
        client.post()
                .uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue("{\"name\":\"Kanye\",\"age\":40,\"email\":\"kanye@ucll.be\",\"password\":\"ye1234555\"}")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("{\"name\":\"Kanye\",\"age\":40,\"email\":\"kanye@ucll.be\",\"password\":\"ye1234555\"}");

        assertEquals("Kanye", userService.findUsersByEmail("kanye@ucll.be").getName());
    }

    @Test
    public void givenExistingUser_whenInvokingPut_thenUserIsUpdated() {
        client.put()
                .uri("/users/jane.toe@ucll.be")
                .header("Content-Type", "application/json")
                .bodyValue("{\"name\":\"Jane Updated\",\"age\":31,\"email\":\"jane.toe@ucll.be\",\"password\":\"updated123\"}")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("{\"name\":\"Jane Updated\",\"age\":31,\"email\":\"jane.toe@ucll.be\",\"password\":\"updated123\"}");

        assertEquals("Jane Updated", userService.findUsersByEmail("jane.toe@ucll.be").getName());
    }

    @Test
    public void givenUser_whenInvokingDelete_thenUserIsDeleted() {
        client.delete()
                .uri("/users/jack.doe@ucll.be")
                .exchange()
                .expectStatus().is2xxSuccessful();

        assertNull(userService.findUsersByEmail("jack.doe@ucll.be"));
    }

    @Test
    public void givenUserWithLoans_whenInvokingGetUserLoans_thenLoansAreReturned() {
        client.get()
                .uri("/users/21.savage@ucll.be/loans")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{" +
                        "\"user\":{\"name\":\"21Savage\",\"age\":25,\"email\":\"21.savage@ucll.be\",\"password\":\"john1234\"}," +
                        "\"publications\":[" +
                        "{" +
                        "\"title\":\"Harry Potter\"," +
                        "\"author\":\"J.K. Rowling\"," +
                        "\"publicationYear\":2001," +
                        "\"availableCopies\":4," +
                        "\"isbn\":\"978-0-545-01022-1\"" +
                        "}" +
                        "]," +
                        "\"startDate\":\"2025-05-08\"," +
                        "\"endDate\":\"2025-05-29\"," + // and this according to todays date + 19
                        "\"returned\":false" +
                        "}" +
                        "]");
    }

}

