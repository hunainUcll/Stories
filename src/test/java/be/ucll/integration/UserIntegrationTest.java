package be.ucll.integration;

import be.ucll.LibraryApplication;
import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.repository.DbInitializer;
import be.ucll.repository.UserRepository;
import be.ucll.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = LibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Sql("classpath:schema.sql")
public class UserIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DbInitializer dbInitializer;
    @BeforeEach
    public void setup() {
        dbInitializer.initialize();
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
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}," +
                        "{\"name\":\"22Savage\",\"age\":25,\"email\":\"22.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for tech.\",\"location\":\"Antwerp\",\"interests\":\"Music, rapping, Basketball\"}}," +
                        "{\"name\":\"23Savage\",\"age\":25,\"email\":\"23.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for activities.\",\"location\":\"brussels\",\"interests\":\"Smoking, rapping, Coding\"}}" +
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
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}," +
                        "{\"name\":\"22Savage\",\"age\":25,\"email\":\"22.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for tech.\",\"location\":\"Antwerp\",\"interests\":\"Music, rapping, Basketball\"}}," +
                        "{\"name\":\"23Savage\",\"age\":25,\"email\":\"23.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for activities.\",\"location\":\"brussels\",\"interests\":\"Smoking, rapping, Coding\"}}" +
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
                        "{\"name\":\"Birgit Doe\",\"age\":18,\"email\":\"birgit.doe@ucll.be\",\"password\":\"birgit1234\"}," +
                        "{\"name\":\"22Savage\",\"age\":25,\"email\":\"22.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for tech.\",\"location\":\"Antwerp\",\"interests\":\"Music, rapping, Basketball\"}}," +
                        "{\"name\":\"23Savage\",\"age\":25,\"email\":\"23.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Aspiring rapper with a love for activities.\",\"location\":\"brussels\",\"interests\":\"Smoking, rapping, Coding\"}}" +
                        "]");
    }


    @Test
    public void givenUser_whenInvokingPost_thenUserIsSaved() {
        client.post()
                .uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue("{\"name\":\"Kanye\",\"age\":40,\"email\":\"kanye@ucll.be\",\"password\":\"ye1234555\",\"profile\":null}")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("{\"name\":\"Kanye\",\"age\":40,\"email\":\"kanye@ucll.be\",\"password\":\"ye1234555\",\"profile\":null}");

        assertEquals("Kanye", userService.findUserByEmail("kanye@ucll.be").getName());
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

        assertEquals("Jane Updated", userService.findUserByEmail("jane.toe@ucll.be").getName());
    }

    @Test
    public void givenUser_whenInvokingDelete_thenUserIsDeleted() {
        client.delete()
                .uri("/users/jack.doe@ucll.be")
                .exchange()
                .expectStatus().is2xxSuccessful();

        assertNull(userService.findUserByEmail("jack.doe@ucll.be"));
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
                        "\"availableCopies\":5," +
                        "\"isbn\":\"978-0-545-01022-1\"" +
                        "}" +
                        "]," +
                        "\"startDate\":\"2025-05-08\"," +
                        "\"endDate\":\"2025-06-07\"," + // and this according to todays date + 19
                        "\"returned\":false" +
                        "}" +
                        "]");
    }

    // sotry 22
    @Test
    public void givenUserWithProfile_whenInvokingPost_thenUserIsSavedWithProfile() {
        client.post()
                .uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue("{\"name\":\"24Savage\",\"age\":25,\"email\":\"24.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Rapper and artist\",\"location\":\"Atlanta\",\"interests\":\"Music, Entrepreneurship\"}}")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("{\"name\":\"24Savage\",\"age\":25,\"email\":\"24.savage@ucll.be\",\"password\":\"john1234\",\"profile\":{\"bio\":\"Rapper and artist\",\"location\":\"Atlanta\",\"interests\":\"Music, Entrepreneurship\"}}");

        assertEquals("24Savage", userService.findUserByEmail("24.savage@ucll.be").getName());
    }


    // story 23
    @Test
    public void givenUsers_whenInvokingGetOldest_thenOnlyOldestIsReturned() {
        client.get()
                .uri("/users/oldest")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("{\"name\":\"Jane Toe\",\"age\":30,\"email\":\"jane.toe@ucll.be\",\"password\":\"jane1234\"}");
    }

    // story 24
    @Test
    public void givenDbInit_whenInvokingGetUsersByInterestRapping_thenReturns22And23Savage() {
        client.get()
                .uri("/users/interest/rapping")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                      [
                          {
                              "name": "22Savage",
                              "age": 25,
                              "email": "22.savage@ucll.be",
                              "password": "john1234",
                              "profile": {
                                  "bio": "Aspiring rapper with a love for tech.",
                                  "location": "Antwerp",
                                  "interests": "Music, rapping, Basketball"
                              }
                          },
                          {
                              "name": "23Savage",
                              "age": 25,
                              "email": "23.savage@ucll.be",
                              "password": "john1234",
                              "profile": {
                                  "bio": "Aspiring rapper with a love for activities.",
                                  "location": "brussels",
                                  "interests": "Smoking, rapping, Coding"
                              }
                          }
                      ]
                      """);
    }


    // Story 25

    @Test
    public void givenDbInit_whenInvokingGetUsersByInterestAndAge_thenReturns22And23Savage() {
        client.get()
                .uri("/users/interest/rapping/20")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .json("""
                    [
                        {
                            "name": "22Savage",
                            "age": 25,
                            "email": "22.savage@ucll.be",
                            "password": "john1234",
                            "profile": {
                                "bio": "Aspiring rapper with a love for tech.",
                                "location": "Antwerp",
                                "interests": "Music, rapping, Basketball"
                            }
                        },
                        {
                            "name": "23Savage",
                            "age": 25,
                            "email": "23.savage@ucll.be",
                            "password": "john1234",
                            "profile": {
                                "bio": "Aspiring rapper with a love for activities.",
                                "location": "brussels",
                                "interests": "Smoking, rapping, Coding"
                            }
                        }
                    ]
                """);
    }


    @Test
    public void givenMembership_whenPostingToUserEmail_thenMembershipIsAdded() {
        // creating a test userr
        client.post()
                .uri("/users")
                .header("Content-Type", "application/json")
                .bodyValue("""
                {
                    "name": "Kanye",
                    "age": 40,
                    "email": "kanye@ucll.be",
                    "password": "ye1234555"
                }
            """)
                .exchange()
                .expectStatus().is2xxSuccessful();

        // the test below will run correctly for 3000 years and then it will fail
        client.post()
                .uri("/users/kanye@ucll.be/membership")
                .header("Content-Type", "application/json")
                .bodyValue("""
                {
                    "startDate": "5025-05-24",
                    "endDate": "5026-05-24",
                    "membershipType": "GOLD"
                }
            """)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.memberships[0].membershipType").isEqualTo("GOLD");

        // Assert the user has the membership
        User updatedUser = userService.findUserByEmail("kanye@ucll.be");
        assertEquals(1, updatedUser.getMemberships().size());
        assertEquals("GOLD", updatedUser.getMemberships().get(0).getMembershipType());
    }



    //story 30

    @Test
    public void givenUserAndPublications_whenPostLoan_thenLoanIsCreatedAndLinkedToUser() {
        // Arrange
        String userEmail = "birgit.doe@ucll.be";
        LocalDate startDate = LocalDate.now();

        // Assume these publication IDs exist in DB, for example: 1 and 2
        List<Long> publicationIds = List.of(1L, 2L);

        client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/users/{email}/loans/{startDate}")
                        .build(userEmail, startDate.toString()))
                .header("Content-Type", "application/json")
                .bodyValue(publicationIds)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.user.email").isEqualTo(userEmail)
                .jsonPath("$.startDate").isEqualTo(startDate.toString())
                .jsonPath("$.publications.length()").isEqualTo(publicationIds.size());

    }




}

