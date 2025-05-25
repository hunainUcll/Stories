package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Domain model for a library membership (Story 29 – free loans).
 */
@Entity
public class Membership {

    /* ---------- columns ---------- */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    /** Only BRONZE / SILVER / GOLD are valid – checked by Hibernate Validator. */
    @NotBlank(message = "Membership type is required.")
    @Pattern(regexp = "BRONZE|SILVER|GOLD", message = "Invalid membership type.")
    private String type;

    /** Remaining free loans for the membership. */
    @Column(name = "free_loans")
    @Min(0)
    private int freeLoans;

    /* ---------- relationship ---------- */
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;

    /* ---------- constructors ---------- */
    /** JPA only */
    protected Membership() {}

    /** Convenience constructor that picks the minimum legal free‑loan balance. */
    public Membership(LocalDate startDate, LocalDate endDate, String type) {
        this(startDate, endDate, type, defaultFreeLoansFor(type));
    }

    /** Full constructor where the caller provides the initial balance. */
    public Membership(LocalDate startDate,
                      LocalDate endDate,
                      String type,
                      int freeLoans) {
        this.startDate = startDate;
        this.endDate   = endDate;
        this.type      = type;

        // Validate free‑loan range *only* for recognised types.
        if (isRecognisedType(type)) {
            setFreeLoans(freeLoans);   // performs the range check
        } else {
            // Unknown type (e.g. during a negative unit‑test): accept raw value.
            this.freeLoans = freeLoans;
        }
    }

    /* ---------- behaviour ---------- */
    /** Redeem a single free loan. */
    public void redeemFreeLoan() {
        if (freeLoans <= 0) {
            throw new RuntimeException("No more free loans available within membership.");
        }
        freeLoans--;  // happy path
    }

    /** Validate & set the remaining balance. */
    public void setFreeLoans(int freeLoans) {
        if (!isValidFreeLoanCount(type, freeLoans)) {
            throw new RuntimeException("Invalid number of free loans for membership type.");
        }
        this.freeLoans = freeLoans;
    }

    /* ---------- helpers ---------- */
    private static boolean isRecognisedType(String type) {
        return switch (type) {
            case "BRONZE", "SILVER", "GOLD" -> true;
            default -> false;
        };
    }

    private static boolean isValidFreeLoanCount(String type, int freeLoans) {
        return switch (type) {
            case "BRONZE" -> freeLoans >= 0  && freeLoans <= 5;
            case "SILVER" -> freeLoans >= 6  && freeLoans <= 10;
            case "GOLD"   -> freeLoans >= 11 && freeLoans <= 15;
            default       -> false;   // only reached for recognised types
        };
    }

    private static int defaultFreeLoansFor(String type) {
        return switch (type) {
            case "BRONZE" -> 0;
            case "SILVER" -> 6;
            case "GOLD"   -> 11;
            default       -> 0;  // fallback for unknown type (keeps constructor simple)
        };
    }

    /* ---------- getters ---------- */
    public Long      getId()        { return id; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate()   { return endDate; }
    public String    getType()      { return type; }
    public int       getFreeLoans() { return freeLoans; }
    public User      getUser()      { return user; }

    /* ---------- relationship helper ---------- */
    public void setUser(User user) { this.user = user; }
}
