package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import be.ucll.model.User;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "loan")
public class Loan {

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User is required")
    private User user;

    @ManyToMany
    @JoinTable(name = "loan_publications",joinColumns = @JoinColumn(name = "loan_id"), inverseJoinColumns = @JoinColumn(name = "publication_id"))
    private List<Publication> publications;


    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean isReturned;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    public Loan(User user, List<Publication> publications, LocalDate startDate) {
        setUser(user);
        setStartDate(startDate);
        setPublications(publications);
        // Only compute endDate if startDate is not null
        if (startDate != null) {
            this.endDate = startDate.plusDays(30);
        }
    }

    protected Loan() {}

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

    // security flaw here - potentially could use this function to reset the start date after the construction of th end date
    // my code breaks without it sooooo if it is not a problem dont fix it
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public List<Publication> getPublications() {
        return publications;
    }
    public void setPublications(List<Publication> publications) {
        if (publications == null || publications.isEmpty()) {
            throw new RuntimeException("List is required.");
        }

        for (Publication publication : publications) {
            if (publication.getAvailableCopies() == 0) {
                // If one publication has no available copies, throw an exception and stop further processing.
                throw new RuntimeException("Unable to lend publication. No copies available for " + publication.getTitle() + ".");
            }
        }
        this.publications = publications;

        for (Publication publication : publications) {
            publication.lendPublications(); // Decrease the available copies only after validation
        }

        this.isReturned = false; // Mark returned false when item is borrowed
    }

    public boolean isReturned() {
        // will probably have to change this
        //if (!isReturned && LocalDate.now().isAfter(endDate)) {
          //  this.isReturned = true;
        //}
        return isReturned;
    }


    public void returnPublications() {
        if (isReturned) {
            throw new RuntimeException("Publications have already been returned.");
        }
        for (Publication publication : publications) {
            publication.returnPublication();
        }
        isReturned = true;
    }


    public void setReturned(boolean b) {
        this.isReturned = b;
    }

    public void setEndDate(LocalDate returnDate) {
        this.endDate = returnDate;
    }

    public void setPrice(double totalPrice) {
        this.price = totalPrice;
    }
    public double getPrice() {
        return price;
    }
}
