package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "User is required.")
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    @NotEmpty(message = "Publications list cannot be empty.")
    @ManyToMany
    @JoinTable(
            name = "loan_publications",
            joinColumns = @JoinColumn(name = "loan_id"),
            inverseJoinColumns = @JoinColumn(name = "publication_id")
    )
    private List<Publication> publications;

    @NotNull(message = "Start date is required.")
    @PastOrPresent(message = "Start date cannot be in the future.")
    private LocalDate startDate;

    private LocalDate endDate;

    /** Has the loan been returned already? */
    @Column(nullable = false)
    private boolean returned = false;

    /** Total price calculated when the loan is returned. */
    @Column(precision = 8, scale = 2)          // e.g. 999 999.99
    private BigDecimal price = BigDecimal.ZERO;

    protected Loan() {}                         // JPA only

    public Loan(User user,
                List<Publication> publications,
                LocalDate startDate) {

        /* always run the same validations used elsewhere in the class */
        setUser(user);
        setPublications(publications);
        setStartDate(startDate);

        /* Story 30 rule – planned end is 30 days after start */
        this.endDate = startDate.plusDays(30);
    }

    /* ---------- behaviour ---------- */
    public void markReturned(LocalDate returnDate, BigDecimal price) {
        this.returned = true;
        this.endDate  = returnDate;             // overwrite planned end
        this.price    = price;
    }

    public boolean isReturned()      { return returned; }
    public BigDecimal getPrice()     { return price; }

    // Getters and setters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public List<Publication> getPublications() { return publications; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setUser(User user) {
        if (user == null) {
            throw new RuntimeException("User is required.");
        }
        this.user = user;
    }

    public void setPublications(List<Publication> publications) {
        if (publications == null || publications.isEmpty())
            throw new RuntimeException("List is required.");

        for (Publication p : publications) {
            if (p.getAvailableCopies() <= 0)
                throw new RuntimeException("No copies available for " + p.getTitle());
            p.lendPublication();
        }
        this.publications = new ArrayList<>(publications);   // ← copy to mutable list
    }


    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new RuntimeException("Start date is required.");
        }
        if (startDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("Start date cannot be in the future.");
        }
        this.startDate = startDate;
    }

    public void returnPublications() {
        for (Publication p : publications) {
            p.returnPublication();
        }
    }
}