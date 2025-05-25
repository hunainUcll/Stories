package be.ucll.service;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import be.ucll.repository.PublicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicationService {
    private final PublicationRepository publicationRepository;

    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> findPublicationsByAvailableCopies(int availableCopies) {
        return publicationRepository.findByAvailableCopiesGreaterThanEqual(availableCopies);
    }

    public Publication addPublication(Publication publication) {
        return publicationRepository.save(publication);
    }

    public List<Publication> findPublicationsByTitleAndType(String title, String type) {
        List<Publication> results = publicationRepository.findByTitleContainingIgnoreCase(title);

        if (type != null) {
            results = results.stream()
                    .filter(p -> type.equalsIgnoreCase("book") && p instanceof Book ||
                            type.equalsIgnoreCase("magazine") && p instanceof Magazine)
                    .toList();
        }
        return results;
    }
}