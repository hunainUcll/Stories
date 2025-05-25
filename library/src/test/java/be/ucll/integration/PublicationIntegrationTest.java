package be.ucll.integration;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class PublicationIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    public void givenPublications_whenFilterByAvailableCopies_thenFilteredPublicationsReturned() {
        webTestClient.get().uri("/publications/stock/5")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Publication.class)
                .hasSize(3);
    }

    @Test
    public void givenPublications_whenFilterByTitleAndType_thenFilteredPublicationsReturned() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/publications")
                        .queryParam("title", "Clean Code") // Match exact title
                        .queryParam("type", "book")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Publication.class)
                .hasSize(1);
    }

    @Test
    public void givenValidBook_whenAddPublication_thenBookIsCreated() {
        String validBookJson = """
        {
            "type": "book",
            "title": "Effective Java",
            "author": "Joshua Bloch",
            "isbn": "9780134685991",
            "publicationYear": 2018,
            "availableCopies": 3
        }
        """;

        webTestClient.post().uri("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validBookJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Effective Java")
                .jsonPath("$.author").isEqualTo("Joshua Bloch")
                .jsonPath("$.id").exists();
    }


    // Test POST /publications (Magazine)
    @Test
    public void givenValidMagazine_whenAddPublication_thenMagazineIsCreated() {
        String validMagazineJson = """
        {
            "type": "magazine",
            "title": "Scientific American",
            "editor": "Laura Helmuth", 
            "issn": "0036-8733",
            "publicationYear": 2023,
            "availableCopies": 4
        }
        """;

        webTestClient.post().uri("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validMagazineJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Scientific American")
                .jsonPath("$.editor").isEqualTo("Laura Helmuth");
    }

    // Negative test for invalid Book
    @Test
    public void givenInvalidBook_whenAddPublication_thenBadRequest() {
        String invalidBookJson = """
        {
            "type": "book",
            "title": "",
            "author": "",
            "isbn": "123", 
            "publicationYear": 2023,
            "availableCopies": 10
        }
        """;

        webTestClient.post().uri("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidBookJson)
                .exchange()
                .expectStatus().isBadRequest();
    }

    // Negative test for invalid Magazine
    @Test
    public void givenInvalidMagazine_whenAddPublication_thenBadRequest() {
        String invalidMagazineJson = """
        {
            "type": "magazine",
            "title": "",
            "editor": "",
            "issn": "invalid",
            "publicationYear": 2050,
            "availableCopies": 4
        """;
    }
}