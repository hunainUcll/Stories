package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import be.ucll.unit.repository.PublicationRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicationServiceTest {

    private PublicationService service;

    @BeforeEach
    void setUp() {
        PublicationRepository publicationRepository = new PublicationRepositoryStub();
        service = new PublicationService(publicationRepository);

        // Add sample books
        Book book1 = new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5);
        book1.setId(1L);
        Book book2 = new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5);
        book2.setId(2L);
        Book book3 = new Book("Potterrrr", "J.K. Rowling", "978-0-545-01032-2", 2001, 9);
        book3.setId(3L);

        // Add sample magazines
        Magazine mag1 = new Magazine("Time", "John Doe", "1234-5678", 2022, 7);
        mag1.setId(4L);
        Magazine mag2 = new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 23);
        mag2.setId(5L);
        Magazine mag3 = new Magazine("Timelessssss", "John Doe", "1234-5678", 2022, 0);
        mag3.setId(6L);

        // Save publications
        publicationRepository.save(book1);
        publicationRepository.save(book2);
        publicationRepository.save(book3);
        publicationRepository.save(mag1);
        publicationRepository.save(mag2);
        publicationRepository.save(mag3);
    }


    // all of these should be considered happy tests
    @Test
    void givenNoFilters_whenGettingPublications_thenReturnAll() {
        List<Publication> results = service.findPublicationsByTitleAndType(null,null);
        assertEquals(6,results.size());

    }

    @Test
    void givenTitleFilter_whenMatchingBookExist_thenReturnsFilteredBooks() {
        List<Publication> results = service.findPublicationsByTitleAndType("Harry",null);
        assertEquals(1,results.size());
        assertEquals("Harry Potter",results.get(0).getTitle());

    }

    // here are happy tests of story 12

    @Test
    void givenTitleAndType_whenMatchExists_thenReturnsFilteredPublication() {
        List<Publication> results = service.findPublicationsByTitleAndType("Harry", "Book");
        assertEquals(1, results.size());
        assertEquals("Harry Potter", results.get(0).getTitle());
    }

    @Test
    void givenTitleOnly_whenMatchExists_thenReturnsMatchingPublications() {
        List<Publication> results = service.findPublicationsByTitleAndType("Harry", null);
        assertFalse(results.isEmpty());
    }

    @Test
    void givenTypeOnly_whenGettingBooks_thenReturnsOnlyBooks() {
        List<Publication> results = service.findPublicationsByTitleAndType(null, "Book");
        assertTrue(results.stream().allMatch(p -> p instanceof Book));
    }

    @Test
    void whenGetAllPublications_thenReturnsAll() {
        List<Publication> results = service.getAllPublications();
        assertEquals(6, results.size());  // Assuming 2 books and 2 magazines
    }

    @Test
    void givenStockLimit_whenPublicationsHaveEnoughCopies_thenReturnsMatching() {
        List<Publication> results = service.getPublicationsAboveStock(2);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(p -> p.getAvailableCopies() >= 2));
    }


    // this is an unhappy test cuz we expect the program to fail "gracefully"
    @Test
    void givenTitleFilter_whenNoMatchingBooksExist_thenReturnsEmptyList() {
        List<Publication> results = service.findPublicationsByTitleAndType("Nonexistent", null);
        assertTrue(results.isEmpty());
    }

    // the next two test use( 1stone) assertrue + isinstanceof  (second one) assertInstance of method so they do the same thing but the second one is more "CLEANER"
    @Test
    void givenTypeFilter_whenGettingBooks_thenReturnsOnlyBooks() {
        List<Publication> results = service.findPublicationsByTitleAndType(null, "Book");
        assertEquals(3, results.size());
        assertTrue(results.get(0) instanceof Book);
    }

    @Test
    void givenTypeFilter_whenGettingMagazines_thenReturnsOnlyMagazines() {
        List<Publication> results = service.findPublicationsByTitleAndType(null, "Magazine");
        assertEquals(3, results.size());
        assertInstanceOf(Magazine.class, results.get(0));
    }

    @Test
    void givenBothFilters_whenMatchingResultsExist_thenReturnsFilteredList() {
        List<Publication> results = service.findPublicationsByTitleAndType("Time", "Magazine");
        assertEquals(3, results.size());
        assertEquals("Time", results.get(0).getTitle());
    }

    // this is an unhappy test cuz we expect the program to fail "gracefully"
    @Test
    void givenBothFilters_whenNoMatchingResults_thenReturnsEmptyList() {
        List<Publication> results = service.findPublicationsByTitleAndType("Harry", "Magazine");
        assertTrue(results.isEmpty());
    }

    // unhappy tests for story 12

    @Test
    void givenTitleAndType_whenNoMatch_thenReturnsEmptyList() {
        List<Publication> results = service.findPublicationsByTitleAndType("Nonexistent", "Book");
        assertTrue(results.isEmpty());
    }

    @Test
    void givenInvalidType_whenNoClassMatches_thenReturnsEmptyList() {
        List<Publication> results = service.findPublicationsByTitleAndType(null, "Newspaper");
        assertTrue(results.isEmpty());
    }

    @Test
    void givenStockLimit_whenNoPublicationMeetsRequirement_thenReturnsEmptyList() {
        List<Publication> results = service.getPublicationsAboveStock(999);
        assertTrue(results.isEmpty());
    }




}
