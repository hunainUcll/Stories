package be.ucll.controller;

import be.ucll.model.Publication;
import be.ucll.service.PublicationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publications")
public class PublicationRestController {
    private final PublicationService publicationService;

    public PublicationRestController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping
    public List<Publication> getPublications(@RequestParam(required = false) String title,
                                             @RequestParam(required = false) String type) {
        return publicationService.findPublicationsByTitleAndType(title, type);
    }
    @GetMapping("/all")
    public List<Publication> getAllPublications() {
        return publicationService.getAllPublications();
    }
    @GetMapping("/stock/{availableCopies}")
    public  List<Publication> getPublicationAboveStock(@PathVariable int availableCopies){

        return publicationService.getPublicationsAboveStock(availableCopies);
    }


}
