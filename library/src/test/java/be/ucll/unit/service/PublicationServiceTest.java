package be.ucll.unit.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import be.ucll.service.PublicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

    @Mock  PublicationRepository publicationRepository;
    @InjectMocks PublicationService publicationService;

    /* --------------------------------------------------------
     * addPublication – happy path
     * ------------------------------------------------------ */
    @Test
    void addPublicationPersistsAndReturnsEntity() {
        Publication book = new Book(
                "Clean Code", "Robert C. Martin",
                "9780132350884", 2008, 5);

        when(publicationRepository.save(book)).thenReturn(book);

        Publication saved = publicationService.addPublication(book);

        assertSame(book, saved);
        verify(publicationRepository).save(book);
    }

    /* --------------------------------------------------------
     * find by available copies
     * ------------------------------------------------------ */
    @Test
    void findPublicationsByAvailableCopiesDelegatesToRepository() {
        Publication mag = new Magazine(
                "NatGeo", "Editor", "1234-5678", 2024, 10);

        when(publicationRepository.findByAvailableCopiesGreaterThanEqual(8))
                .thenReturn(List.of(mag));

        List<Publication> result =
                publicationService.findPublicationsByAvailableCopies(8);

        assertEquals(1, result.size());
        assertSame(mag, result.get(0));
    }

    /* --------------------------------------------------------
     * find by title *and* type = BOOK
     * ------------------------------------------------------ */
    @Test
    void findByTitleAndTypeFiltersBooks() {
        Publication book = new Book("Clean Code", "Bob", "9780132350884", 2008, 3);
        Publication mag  = new Magazine("Clean City", "Ed", "1234", 2023, 5);

        when(publicationRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(book, mag));

        List<Publication> result =
                publicationService.findPublicationsByTitleAndType("clean", "book");

        assertEquals(1, result.size());
        assertTrue(result.get(0) instanceof Book);
    }

    /* --------------------------------------------------------
     * find by title with null type — returns all matches
     * ------------------------------------------------------ */
    @Test
    void findByTitleWithNullTypeReturnsAllMatches() {
        Publication book = new Book("Clean Code", "Bob", "9780132350884", 2008, 3);
        Publication mag  = new Magazine("Clean City", "Ed", "1234", 2023, 5);

        when(publicationRepository.findByTitleContainingIgnoreCase("clean"))
                .thenReturn(List.of(book, mag));

        List<Publication> result =
                publicationService.findPublicationsByTitleAndType("clean", null);

        assertEquals(2, result.size());
    }
}
