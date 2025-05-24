package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.Period;

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

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    protected Membership() {}

    public Membership(LocalDate startDate, LocalDate endDate, String membershipType) {
        if (!endDate.equals(startDate.plusYears(1))) {
            throw new IllegalArgumentException("End date must be 1 year after the start date.");
        }
        setStartDate(startDate);
        setEndDate(endDate);
        this.membershipType = membershipType;
    }

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
        if ((this.startDate != null && this.endDate != null && Period.between(this.startDate, this.endDate).getYears() < 1)) {
            throw new IllegalArgumentException("End date must be 1 year after the start date.");
        }
        this.endDate = endDate;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
