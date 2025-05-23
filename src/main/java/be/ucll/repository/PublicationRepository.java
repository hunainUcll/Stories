package be.ucll.repository;

import be.ucll.model.Book;
import be.ucll.model.Loan;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import jakarta.persistence.Column;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {


    List<Publication> findPublicationsByTitleContainingIgnoreCaseAndType(String title, String type);
    List<Publication> findPublicationsByTitleContainingIgnoreCase(String title);


    List<Publication> findPublicationsByAvailableCopiesAfter(int availableCopies);

}
