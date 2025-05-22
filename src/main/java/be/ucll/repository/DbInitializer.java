package be.ucll.repository;

import be.ucll.model.Profile;
import be.ucll.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInitializer {

    private final UserRepository userRepository;
    private  final ProfileRepository profileRepository;

    public DbInitializer(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
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
    }
}
