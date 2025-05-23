package be.ucll.service;

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

    public List<Publication> getAllPublications() {
        List<Publication> publications = publicationRepository.findAll();
        return publications;
    }

    public List<Publication> findPublicationsByTitleAndType(String title, String type) {
        // a little dirty code but tests all cases here
        if ((title == null || title.isBlank()) && (type == null || type.isBlank())) {
            return publicationRepository.findAll();

        } else if (title != null && !title.isBlank() && type != null && !type.isBlank()) {
            return publicationRepository.findPublicationsByTitleContainingIgnoreCaseAndType(title, type);
        } else if (title != null && !title.isBlank()) {
            return publicationRepository.findPublicationsByTitleContainingIgnoreCase(title);
        } else {
            return publicationRepository.findPublicationsByTitleContainingIgnoreCaseAndType(title, type);
        }
    }



    // even here can throw an extra exception if available copies are below 0 but wasnt asked in
    public List<Publication> getPublicationsAboveStock(int availableCopies) {
        return  publicationRepository.findPublicationsByAvailableCopiesAfter(availableCopies);
    }
}
