package be.ucll.repository;

import be.ucll.model.Book;
import be.ucll.model.Magazine;
import be.ucll.model.Publication;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PublicationRepository {
    // had to remove final cuz then can make method to reset data STORY 18
    private List<Book> books;
    private List<Magazine> magazines;

    public PublicationRepository() {
        books = new ArrayList<>(List.of(
                new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5),
                new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5)
        ));

        magazines = new ArrayList<>(List.of(
                new Magazine("Time", "John Doe", "1234-5678", 2022, 7),
                new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 23)
        ));
    }

    public List<Publication> getAllPublications() {
        List<Publication> allPublications = new ArrayList<>();
        allPublications.addAll(books);
        allPublications.addAll(magazines);
        return allPublications;
    }

    public List<Publication> filterPublicationByTitleAndType(String title, String type){
        return  getAllPublications().stream()
                .filter(pub -> (title == null || pub.getTitle().toLowerCase().contains(title.toLowerCase())))
                .filter(pub -> (type == null || pub.getClass().getSimpleName().equalsIgnoreCase(type)))
                .collect(Collectors.toList());
    }

    public List<Publication> filterPublicationsAboveCertainStock(int availableCopies){
        return   getAllPublications().stream()
                .filter(pub ->(pub.getAvailableCopies() >= availableCopies))
                .collect(Collectors.toList());
    }


    public void resetRepositoryData() {
        books = new ArrayList<>(List.of(
                new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5),
                new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5)
        ));

        magazines = new ArrayList<>(List.of(
                new Magazine("Time", "John Doe", "1234-5678", 2022, 7),
                new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 7)
        ));
    }
}
