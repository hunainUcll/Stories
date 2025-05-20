package be.ucll.service;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.repository.LoanRepository;
import be.ucll.repository.ProfileRepository;
import be.ucll.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private UserRepository userRepository;
    private LoanRepository loanRepository;
    private ProfileRepository profileRepository;

    @Autowired
    public UserService(UserRepository userRepository, LoanRepository loanRepository,ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.profileRepository = profileRepository;
    }

    public List<User> getAllUsers(){
        List<User> users = userRepository.findAll();
        if (users == null || users.isEmpty()) {
            throw new RuntimeException("No users found in the repository.");
        }
        return users;
    }

    public List<User> getAllAdultUsers() {
        return userRepository.findUsersByAgeAfter(17);
    }

    public List<User> getUsersBetweenAges(int min, int max) {
        if(min >= max){
            throw new IllegalArgumentException("Minimum age cannot be greater than maximum age.");
        }
        if(min < 0 || max > 150){
            throw new IllegalArgumentException("Invalid age range. Age must be between 0 and 150.");
        }
        return userRepository.findUsersByAgeBetween(min, max);
    }

    public List<User> getUsersByName(String name) {
        List<User> foundUsers = userRepository.findUsersByName(name);
        if (foundUsers.isEmpty()) {
            throw new RuntimeException("No users found with the specified name.");
        }
        return foundUsers;
    }
    // not in project making this to double tap weather teh user is actually gone story 17
    public boolean userExists(String email){
        return userRepository.existsUserByEmail(email);
    }

    public User registerUser(User user) {
        if(userRepository.existsUserByEmail(user.getEmail())){
            throw new RuntimeException("User already exists.");
        }
        if(user.getProfile() != null){
            profileRepository.save(user.getProfile());
        }
        return userRepository.save(user);
    }


    public User updateUser(User user, String email) {
        if(!userRepository.existsUserByEmail(user.getEmail())){
            throw new RuntimeException("User does not exist");
        }

        User updateduser = userRepository.findUserByEmail(email);
        updateduser.updateUser(user.getName(),user.getAge(),user.getEmail(),user.getPassword());
        userRepository.save(updateduser);
        return updateduser;
    }

    public String deleteUser(String email) {
            if (!userRepository.existsUserByEmail(email)) {
                throw new RuntimeException("User does not exist");
            }
            List<Loan> activeLoans = loanRepository.findLoanByUser(email, true);
            if (!activeLoans.isEmpty()) {
                throw new RuntimeException("User has active loans.");
            }

            User user = userRepository.findUserByEmail(email);

            loanRepository.deleteUserLoans(email);
            userRepository.delete(user);
            return "User successfully deleted";
        }

    public User findUsersByEmail(String mail) {
        return userRepository.findUserByEmail(mail);
    }
}

