package be.ucll.repository;

import be.ucll.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInitializer {

    private final UserRepository userRepository;
    private  final ProfileRepository profileRepository;
    private final PublicationRepository publicationRepository;

    public DbInitializer(UserRepository userRepository, ProfileRepository profileRepository, PublicationRepository publicationRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.publicationRepository = publicationRepository;
    }

    @PostConstruct
    public void initialize() {
        Profile profile22Savage = new Profile(
                "Aspiring rapper with a love for tech.",
                "Antwerp",
                "Music, rapping, Basketball"
        );
        Profile profile23Savage = new Profile(
                "Aspiring rapper with a love for activities.",
                "brussels",
                "Smoking, rapping, Coding"
        );
        profileRepository.save(profile22Savage);
        profileRepository.save(profile23Savage);


        List<User> users = List.of(
                new User("21Savage", 25, "21.savage@ucll.be", "john1234"),
                new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234"),
                new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234"),
                new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234"),
                new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234"),
                new User("22Savage", 25, "22.savage@ucll.be", "john1234", profile22Savage),
                new User("23Savage", 25, "23.savage@ucll.be", "john1234", profile23Savage)
        );
        userRepository.saveAll(users);


        // Save books
        List<Publication> books = List.of(
                new Book("Harry Potter", "J.K. Rowling", "978-0-545-01022-1", 2001, 5),
                new Book("Potter", "J.K. Rowling", "978-0-545-01032-1", 2001, 5)
        );

        // Save magazines
        List<Publication> magazines = List.of(
                new Magazine("Time", "John Doe", "1234-5678", 2022, 7),
                new Magazine("TimeLess", "Jones Doe", "1234-5698", 2022, 7)
        );

        publicationRepository.saveAll(books);
        publicationRepository.saveAll(magazines);

    }



}
