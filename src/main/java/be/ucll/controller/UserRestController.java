package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.User;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/all")  //http://localhost:8080/users/all
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/adults") //http://localhost:8080/users/adults
    public List<User> getAllAdultUsers(){
        return userService.getAllAdultUsers();
    }

    @GetMapping("/age/{min}/{max}") //http://localhost:8080/users/age/4/10
    public List<User> getUsersbetweenAges(@PathVariable int min,@PathVariable int max){
        return userService.getUsersBetweenAges(min,max);
    }

    @GetMapping // http://localhost:8080/users?name=doe
    public List<User> getUsers(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return userService.getAllUsers();
        }
        return userService.getUsersByName(name);
    }

    // i donot know the use of responce entity from my research its for custom headers and returnintg different status codes and error messages
    @GetMapping("/{email}/loans")  // http://localhost:8080/users/jane.toe@ucll.be/loans?onlyActive=true
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

    // Story 19
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({RuntimeException.class})
    public Map<String,String> handleRuntimeException(RuntimeException ex){
       Map<String,String> errors = new HashMap<>();
       errors.put("error: ",ex.getMessage());
       return errors;
    }







}
