package be.ucll.repository;


import be.ucll.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    public List<User> users;

    public UserRepository() {
        users = new ArrayList<>(List.of(
                new User("21Savage", 25, "21.savage@ucll.be", "john1234"),
                new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234"),
                new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234"),
                new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234"),
                new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234")
        ));
    }

    public List<User> allUsers() {
        return users;
    }

    public List<User> usersOlderThan(int age){
        List<User> end = new ArrayList<>();
        for (User user : users){
            if(user.getAge() >= age ){
                end.add(user);
            }
        }
        return end;
    }


    // much more convinient way to do the above with chatgpt
    /*public List<User> usersOlderThan(int age) {
        return users.stream()
                .filter(user -> user.getAge() >= age)
                .collect(Collectors.toList());
    }*/

    public List<User> findUsersByAge(int min, int max) {

        List<User> end = new ArrayList<>();
        for(User user:users){
            if(user.getAge() >= min && user.getAge() <= max){
                end.add(user);
            }
        }
        return end;
    }

    public List<User> usersByName(String name) {
        if (name == null || name.isEmpty()) {
            return users;
        }
        return users.stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


    public boolean userExists(String email) {
        for(User user : users){
            if(user.getEmail().equals(email)){
                return true;
            }
        }
        return false;
    }

    public User addUser(User user) {
        users.add(user);
        return user;
    }

    public User findUsersByEmail(String email) {
        for(User user : users){
            if(user.getEmail().equals(email)){
                return user;
            }
        }
        return null;
    }

    // fancy trick intellij suggestion told me to use instead of a boring for loop
    public void deleteUser(String email) {
        users.removeIf(user -> user.getEmail().equals(email));
    }

    public void resetRepositoryData() {
        users = new ArrayList<>(List.of(
                new User("21Savage", 25, "21.savage@ucll.be", "john1234"),
                new User("Jane Toe", 30, "jane.toe@ucll.be", "jane1234"),
                new User("Jack Doe", 5, "jack.doe@ucll.be", "jack1234"),
                new User("Sarah Doe", 4, "sarah.doe@ucll.be", "sarah1234"),
                new User("Birgit Doe", 18, "birgit.doe@ucll.be", "birgit1234")
        ));
    }
}
