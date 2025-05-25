package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "membership")
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start date is required.")
    @FutureOrPresent(message = "Start date must be equal or after today.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;

    @NotNull(message = "Membership type is required.")
    @Pattern(regexp = "BRONZE|SILVER|GOLD", message = "Invalid membership type.")
    private String membershipType;

    @Column(name = "free_loans")
    private int freeLoans;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    protected Membership() {}

    public Membership(LocalDate startDate, LocalDate endDate, String membershipType, int freeLoans) {
        if (!endDate.equals(startDate.plusYears(1))) {
            throw new IllegalArgumentException("End date must be 1 year after the start date.");
        }
        setStartDate(startDate);
        setEndDate(endDate);
        setMembershipType(membershipType);
        setFreeLoans(freeLoans);
    }

    // Getter and setter methods

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (this.startDate != null) {
            long daysBetween = ChronoUnit.DAYS.between(this.startDate, endDate);
            if (daysBetween < 365) {
                throw new RuntimeException("End date must be 1 year after the start date.");
            }
        }
        this.endDate = endDate;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public int getFreeLoans() {
        return freeLoans;
    }

    public void setFreeLoans(int freeLoans) {
        validateFreeLoansForType(this.membershipType, freeLoans);
        this.freeLoans = freeLoans;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    //story 29

    public void redeemFreeLoan() {
        if (this.freeLoans <= 0) {
            throw new IllegalStateException("No more free loans available within membership.");
        }
        this.freeLoans--;
    }

    public void validateFreeLoansForType(String membershipType, int freeLoans) {
        switch (membershipType.toUpperCase()) {
            case "BRONZE":
                if (freeLoans < 0 || freeLoans > 5) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
            case "SILVER":
                if (freeLoans < 6 || freeLoans > 10) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
            case "GOLD":
                if (freeLoans < 11 || freeLoans > 15) {
                    throw new IllegalArgumentException("Invalid number of free loans for membership type.");
                }
                break;
        }
    }

}
