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

    // can throw an extra exception here if the type isnt a book or publication
    public List<Publication> findPublicationsByTitleAndType(String title, String type) {
        return publicationRepository.filterPublicationByTitleAndType(title, type);
    }

    public List<Publication> getAllPublications() {
        return publicationRepository.getAllPublications();
    }

    // even here can throw an extra exception if available copies are below 0
    public List<Publication> getPublicationsAboveStock(int availableCopies) {
        return  publicationRepository.filterPublicationsAboveCertainStock(availableCopies);
    }
}
