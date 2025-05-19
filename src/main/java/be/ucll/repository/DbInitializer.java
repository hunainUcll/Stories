package be.ucll.repository;

import be.ucll.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class DbInitializer {

    private final UserRepository userRepository;

    public DbInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void initialize(){

              User user1   =new User("21Savage", 25, "21.savage@ucll.be", "john1234");
              User user2   =new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234");
              User user3   =new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234");
              User user4   =new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234");
              User user5   =new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234");


        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);


    }




}
