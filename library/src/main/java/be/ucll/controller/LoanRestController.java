package be.ucll.controller;

import be.ucll.model.Loan;
import be.ucll.model.Publication;
import be.ucll.model.User;
import be.ucll.repository.PublicationRepository;
import be.ucll.repository.UserRepository;
import be.ucll.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/loans")
public class LoanRestController {
    private final LoanService loanService;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    public LoanRestController(LoanService loanService,
                              UserRepository userRepository,
                              PublicationRepository publicationRepository) {
        this.loanService = loanService;
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Loan createLoan(@RequestBody Map<String, Object> request) {
        // 1. Validate and extract request data
        String userEmail = (String) request.get("userEmail");
        List<Integer> publicationIds = (List<Integer>) request.get("publicationIds");
        String startDateStr = (String) request.get("startDate");

        if (userEmail == null || publicationIds == null || startDateStr == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required fields");
        }

        // 2. Find user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found with email: " + userEmail
                ));

        // 3. Find publications
        List<Publication> publications = publicationIds.stream()
                .map(id -> publicationRepository.findById(id.longValue())
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Publication not found with id: " + id
                        )))
                .collect(Collectors.toList());

        // 4. Create and return loan
        try {
            LocalDate startDate = LocalDate.parse(startDateStr);
            return loanService.addLoan(new Loan(user, publications, startDate));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }
    }
}