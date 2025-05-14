package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;  // Inject LoanRepository

    @Autowired
    public UserService(UserRepository userRepository, LoanRepository loanRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
    }

    public List<User> getAllUsers(){
        List<User> users = userRepository.allUsers();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("No users found in the repository.");
        }
        return users;
    }

    public List<User> getAllAdultUsers() {
        return userRepository.usersOlderThan(18);
    }

    public List<User> getUsersBetweenAges(int min, int max) {
        if(min >= max){
            throw new IllegalArgumentException("Minimum age cannot be greater than maximum age.");
        }
        if(min < 0 || max > 150){
            throw new IllegalArgumentException("Invalid age range. Age must be between 0 and 150.");
        }
        return userRepository.findUsersByAge(min,max);
    }

    public List<User> getUsersByName(String name) {
        List<User> foundUsers = userRepository.usersByName(name);
        if (foundUsers.isEmpty()) {
            throw new RuntimeException("No users found with the specified name.");
        }
        return foundUsers;
    }
    // not in project making this to double tap weather teh user is actually gone story 17
    public boolean userExists(String email){
        return userRepository.userExists(email);
    }

    public User registerUser(User user) {
        if(userRepository.userExists(user.getEmail())){
            throw new RuntimeException("User already exists.");
        }
        return userRepository.addUser(user);
    }


    public User updateUser(User user, String email) {
        if(!userRepository.userExists(user.getEmail())){
            throw new RuntimeException("User does not exist");
        }

        User updateduser = userRepository.findUsersByEmail(email);
        updateduser.updateUser(user.getName(),user.getAge(),user.getEmail(),user.getPassword());
        return updateduser;
    }

    public String deleteUser(String email) {
            if (!userRepository.userExists(email)) {
                throw new RuntimeException("User does not exist");
            }
            List<Loan> activeLoans = loanRepository.findLoanByUser(email, true);
            if (!activeLoans.isEmpty()) {
                throw new RuntimeException("User has active loans.");
            }
            loanRepository.deleteUserLoans(email);
            userRepository.deleteUser(email);
            return "User successfully deleted";
        }

    public User findUsersByEmail(String mail) {
        return userRepository.findUsersByEmail(mail);
    }
}

