package be.ucll.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type",discriminatorType = DiscriminatorType.STRING)
public abstract class Publication {
    @NotBlank(message = "title is required.")
    private String title;

    @Min(value = 1,message = "year cannot be 0 or negative")
    private int publicationYear;

    @PositiveOrZero(message = "Available copies must not be a negative number.")
    private int availableCopies;

    // for unit testing purposes
    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(insertable = false, updatable = false)
    private String type;
    public String getType() {
        return type;
    }

    public Publication(String title, int publicationYear, int availableCopies) {
        setTitle(title);
        setPublicationYear(publicationYear);
        setAvailableCopies(availableCopies);
    }

    protected Publication() {}

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {this.title = title;}

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        if (publicationYear > currentYear) {throw new RuntimeException("Publication year cannot be in the future.");}
        this.publicationYear = publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }
    public void setAvailableCopies(int availableCopies) {this.availableCopies = availableCopies;}

    public void lendPublications(){
        //possible error here so test the loan and we might have to remove this check later on
        if (availableCopies < 0){throw new RuntimeException("No more copies available");}
        this.availableCopies --;
    }

    public void returnPublication() {
        availableCopies++; // Increment available copies on return
    }

    public Long getId() {
        return id;
    }
}