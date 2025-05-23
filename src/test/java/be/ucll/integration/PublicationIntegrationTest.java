package be.ucll.integration;

import be.ucll.LibraryApplication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(classes = LibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PublicationIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private PublicationService publicationService;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    public void givenPublications_whenInvokingGetPublicationsByTitleAndType_thenCorrectPublicationsAreReturned() {
        // Create some test publications first (make sure they're added to the repository or mock service)
        // For now, assuming they are pre-populated in the repository or set by your service.

        client.get()
                .uri("/publications?title=Harry Potter&type=book")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{" +
                        "\"title\":\"Harry Potter\"," +
                        "\"author\":\"J.K. Rowling\"," +
                        "\"publicationYear\":2001," +
                        "\"availableCopies\":5," +
                        "\"isbn\":\"978-0-545-01022-1\"" +
                        "}" +
                        "]");
    }

    //ERRROR HERE for now i used a work around the problem is everytime the project is built the loans are made and the available copies are reduced thus the test expects teh initial available copies which wont work cuz they are changed
    // so for now i changed available copies of a publication which isnt loaned out to 23 and made the http request to stock above 20
    // NVM the error fixed itself
    // when i test only the snippet the loans are called out and the stock is reduced
    // when i test the whole thing the aftereach kicks in and resets the repo
    // so no problem after all
    // lol
    @Test
    public void givenPublications_whenInvokingGetPublicationsByStock_thenCorrectPublicationsAreReturned() {
        // Reset data or make sure some copies are loaned out if needed
        // Here, we are expecting publications with at least 6 available copies after loaning out a few

        client.get()
                .uri("/publications/stock/6")  // Expecting publications with at least 6 availableCopies
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .json("[" +
                        "{" +
                        "\"title\":\"Time\"," +
                        "\"editor\":\"John Doe\"," +
                        "\"publicationYear\":2022," +
                        "\"availableCopies\":7," +
                        "\"issn\":\"1234-5678\"" +
                        "}," +
                        "{" +
                        "\"title\":\"TimeLess\"," +
                        "\"editor\":\"Jones Doe\"," +
                        "\"publicationYear\":2022," +
                        "\"availableCopies\":7," +
                        "\"issn\":\"1234-5698\"" +
                        "}" +
                        "]");
    }

}

