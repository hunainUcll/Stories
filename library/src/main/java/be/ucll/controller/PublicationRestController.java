package be.ucll.controller;

import be.ucll.model.Publication;
import be.ucll.service.PublicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publications")
public class PublicationRestController {

    private final PublicationService publicationService;

    public PublicationRestController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Publication addPublication(@Valid @RequestBody Publication publication) {
        return publicationService.addPublication(publication);
    }

    @GetMapping("/stock/{availableCopies}")
    public List<Publication> getPublicationsByAvailableCopies(@PathVariable int availableCopies) {
        return publicationService.findPublicationsByAvailableCopies(availableCopies);
    }

    @GetMapping
    public List<Publication> getPublicationsByTitleAndType(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String type) {
        // Implementation would go in PublicationService
        return publicationService.findPublicationsByTitleAndType(title, type);
    }
}