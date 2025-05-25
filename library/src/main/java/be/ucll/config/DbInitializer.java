package be.ucll.config;

import be.ucll.repository.*;
import be.ucll.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DbInitializer {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final PublicationRepository publicationRepository;
    private final MembershipRepository membershipRepository;
    private final LoanRepository loanRepository;

    public DbInitializer(UserRepository userRepository, ProfileRepository profileRepository, PublicationRepository publicationRepository, MembershipRepository membershipRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.publicationRepository = publicationRepository;
        this.membershipRepository = membershipRepository;
        this.loanRepository = loanRepository;
    }

    @PostConstruct
    public void initialize() {
        User adult1 = new User("John Doe", "john1234", "john.doe@ucll.be", 25);
        Profile profile1 = new Profile("Teacher at UCLL", "Leuven", "Science, reading, cooking, movies");
        adult1.setProfile(profile1);
        userRepository.save(adult1);

        User adult2 = new User("Jane Toe", "jane1234", "jane.toe@ucll.be", 30);
        Profile profile2 = new Profile("Student at UCLL", "Brussels", "Science, music");
        adult2.setProfile(profile2);  // Save profile first
        userRepository.save(adult2);

        // Adult user without profile
        User adult3 = new User("Birgit Doe", "birgit1234", "birgit.doe@ucll.be", 18);
        userRepository.save(adult3);

        // Minor users (no profiles)
        User minor1 = new User("Jack Doe", "jack1234", "jack.doe@ucll.be", 5);
        userRepository.save(minor1);

        User minor2 = new User("Sarah Doe", "sarah1234", "sarah.doe@ucll.be", 4);
        userRepository.save(minor2);

        Membership m1 = new Membership(
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(1).plusYears(1),
                "GOLD",
                12         // valid: 11 – 15
        );
        m1.setUser(adult1);
        membershipRepository.save(m1);

        Membership m2 = new Membership(
                LocalDate.now().minusDays(10),     // started 10 days ago
                LocalDate.now().plusYears(1),      // valid for another year
                "SILVER",
                8                                   // any value 6-10 is fine
        );
        m2.setUser(adult2);
        membershipRepository.save(m2);

        Publication book1 = new Book("Clean Code", "Robert C. Martin", "9780132350884", 2008, 5);
        Publication book2 = new Book("The Lord of the Rings", "J.R.R. Tolkien", "9780544003415", 1954, 3);
        Publication book3 = new Book("Another Book", "Author", "1234567890", 2020, 6);
        Publication magazine1 = new Magazine("National Geographic", "Susan Goldberg", "1234-5678", 2022, 10);
        Publication magazine2 = new Magazine("Time", "Edward Felsenthal", "0040-781X", 1923, 7);
        Loan active   = new Loan(adult2,
                List.of(book1, book3),
                LocalDate.now().minusDays(5));           // active (-5 … +25)

        Loan inactive = new Loan(adult3,
                List.of(magazine2),
                LocalDate.now().minusDays(40));

        publicationRepository.save(book1);
        publicationRepository.save(book2);
        publicationRepository.save(book3);
        publicationRepository.save(magazine1);
        publicationRepository.save(magazine2);
        loanRepository.save(active);
        loanRepository.save(inactive);
    }
}