package be.ucll.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.List;

public class Loan {

    @NotNull(message = "User is required")
    private User user;

    private List<Publication> publications;

    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;


    private LocalDate endDate;

    private Boolean isReturned;

    public Loan(User user, List<Publication> publications, LocalDate startDate) {
        setUser(user);
        setStartDate(startDate);
        setPublications(publications);
        // Only compute endDate if startDate is not null
        if (startDate != null) {
            this.endDate = startDate.plusDays(21);
        }
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {this.user = user;}

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public List<Publication> getPublications() {
        return publications;
    }
    public void setPublications(List<Publication> publications) {
        if (publications == null || publications.isEmpty()) {
            throw new RuntimeException("List is required.");
        }

        // First, check if all publications have available copies.
        for (Publication publication : publications) {
            if (publication.getAvailableCopies() == 0) {
                // If one publication has no available copies, throw an exception and stop further processing.
                throw new RuntimeException("Unable to lend publication. No copies available for " + publication.getTitle() + ".");
            }
        }

        // Now that we've validated, set the publications
        this.publications = publications;

        // Now, decrement the available copies for each publication (since all are valid)
        for (Publication publication : publications) {
            publication.lendPublications(); // Decrease the available copies only after validation
        }

        this.isReturned = false; // Mark returned false when item is borrowed
    }

    public boolean isReturned() {
        // will probably have to change this
        if (!isReturned && LocalDate.now().isAfter(endDate)) {
            this.isReturned = true;
        }
        return isReturned;
    }


    public void returnPublications() {
        if (isReturned) {
            throw new RuntimeException("Publications have already been returned.");
        }

        for (Publication publication : publications) {
            publication.returnPublication();
        }

        isReturned = true; // Mark loan as returned
    }


}
