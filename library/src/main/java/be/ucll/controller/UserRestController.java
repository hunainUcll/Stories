package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.Membership;
import be.ucll.model.User;
import be.ucll.service.LoanService;
import be.ucll.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final LoanService loanService;

    public UserRestController(UserService userService, LoanService loanService) {
        this.userService = userService;
        this.loanService = loanService;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestParam(required = false) String name) {
        if (name == null || name.isEmpty()) {
            return userService.getAllUsers();
        } else {
            return userService.getUsersByName(name);
        }
    }

    @GetMapping("/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @GetMapping("/{email}/loans")
    public List<Loan> getLoansForUser(
            @PathVariable String email,
            @RequestParam(required = false, defaultValue = "false") boolean onlyActive
    ) {
        return loanService.getLoansForUser(email, onlyActive);
    }

    @GetMapping("/{email}/membership")
    @ResponseStatus(HttpStatus.OK)
    public Membership getMembershipOnDate(
            @PathVariable String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return userService.getMembershipByDate(email, date);
    }


    @GetMapping("/adults")
    public List<User> getAllAdultUsers() {
        return userService.getAllAdultUsers();
    }

    @GetMapping("/age/{min}/{max}")
    public List<User> getUsersInAgeRange(@PathVariable int min, @PathVariable int max) {
        return userService.getUsersInAgeRange(min, max);
    }

    @GetMapping("/oldest")
    public User getOldestUser() {
        return userService.getOldestUser();
    }

    @GetMapping("/interest/{interest}")
    public List<User> getUsersByInterest(@PathVariable String interest) {
        return userService.getUsersByInterest(interest);
    }

    @GetMapping("/interest/{interest}/{age}")
    public List<User> getUsersByInterestAndAge(
            @PathVariable String interest,
            @PathVariable int age
    ) {
        return userService.getUsersByInterestAndAge(interest, age);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping("/{email}/membership")
    public User addMembership(
            @PathVariable String email,
            @Valid @RequestBody Membership membership
    ) {
        return userService.addMembership(email, membership);
    }

    @PostMapping("/{email}/loans/{startDate}")
    @ResponseStatus(HttpStatus.OK)
    public Loan registerLoan(
            @PathVariable String email,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestBody Map<String, List<Long>> body) {

        List<Long> publicationIds = body.get("publicationIds");
        if (publicationIds == null || publicationIds.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "publicationIds list is required.");

        return loanService.registerLoan(email, startDate, publicationIds);
    }

    @PutMapping("/{email}")
    public User updateUser(
            @PathVariable String email,
            @Valid @RequestBody User updatedUser
    ) {
        return userService.updateUser(email, updatedUser);
    }

    @PutMapping("/{email}/loans/return/{returnDate}")
    public Loan returnLoan(
            @PathVariable String email,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate) {

        return loanService.returnLoan(email, returnDate);
    }

    @DeleteMapping("/{email}")
    public Map<String, String> deleteUser(@PathVariable String email) {
        userService.deleteUser(email);
        return Map.of("message", "User deleted successfully");
    }

    @DeleteMapping("/{email}/loans")
    public String deleteUserLoans(@PathVariable String email) {
        return loanService.deleteLoansByUserEmail(email);
    }
}