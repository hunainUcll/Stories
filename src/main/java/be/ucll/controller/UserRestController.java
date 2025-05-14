package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final LoanService loanService;

    @Autowired
    public UserRestController(UserService userService, LoanService loanService){
        this.userService = userService;
        this.loanService = loanService;
    }

    @GetMapping("/all")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/adults")
    public List<User> getAllAdultUsers(){
        return userService.getAllAdultUsers();
    }

    @GetMapping("/age/{min}/{max}")
    public List<User> getUsersbetweenAges(@PathVariable int min,@PathVariable int max){
        return userService.getUsersBetweenAges(min,max);
    }

    @GetMapping
    public List<User> getUsers(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return userService.getAllUsers();
        }
        return userService.getUsersByName(name);
    }

    // i donot know the use of responce entity from my research its for custom headers and returnintg different status codes and error messages
    @GetMapping("/{email}/loans")
    public ResponseEntity<List<Loan>> getLoansForUser(@PathVariable String email, @RequestParam(required = false) boolean onlyActive) {
        List<Loan> loans = loanService.getLoansByUser(email, onlyActive);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PutMapping("/{email}")
    public User updateUser(@RequestBody User user, @PathVariable String email){
        return userService.updateUser(user,email);
    }

    @DeleteMapping("/{email}/loans")
    public String deleteUserLoans(@PathVariable String email){
       return loanService.deleteUserLoans(email);
    }
    @DeleteMapping("/{email}")
    public String deleteUser(@PathVariable String email){
        return userService.deleteUser(email);
    }






}
