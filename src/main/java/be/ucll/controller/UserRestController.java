package be.ucll.controller;

import be.ucll.model.*;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    // refactoring code here to better fit Story 28
    @GetMapping("/{email}/loans")                                                                                                                   // http://localhost:8080/users/jane.toe@ucll.be/loans?onlyActive=true
    public ResponseEntity<List<Loan>> getLoansForUser(@PathVariable String email, @RequestParam(required = false) boolean onlyActive) {
        List<Loan> loans = loanService.getLoansByUser(email, onlyActive);
        if (loans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(loans);
    }

    @PostMapping
    public User registerUser(@Valid @RequestBody User user){
        return userService.registerUser(user);
    }

    @PutMapping("/{email}")
    public User updateUser(@Valid @RequestBody User user, @PathVariable String email){
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

    // story 20 only the exception handler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public Map<String, String> handleValidationException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        for(FieldError error : ex.getFieldErrors()){
            String fieldName = error.getField();
            String fieldError = error.getDefaultMessage();
            errors.put(fieldName,fieldError);
        }
        return errors;
    }

    // Story 23

    @GetMapping("/oldest")
    public User getOldestUser(){
        return userService.getOldestUser();
    }

    // story 24

    @GetMapping("/interest/{interest}")
    public List<User> getUsersByInterests(@PathVariable String interest){
        return userService.getUsersByInterest(interest);
    }

    @GetMapping("/interest/{interest}/{age}")
    public List<User> getUsersByInterestAndAboveAge(@PathVariable String interest, @PathVariable int age){
        return userService.getUsersByInterestAndAboveAge(interest,age);

    }
    @PostMapping("/{email}/membership")
    public User addMembership(@PathVariable String email, @RequestBody @Valid Membership membership) {
        return userService.addMembership(email,membership);
    }

    //story 30
    @PostMapping("/{email}/loans/{startDate}")
    public Loan setLoanForUser(@PathVariable String email, @PathVariable LocalDate startDate,@RequestBody List<Long> publicationIds){
        return loanService.setLoanForUser(email,startDate,publicationIds);
    }









}
