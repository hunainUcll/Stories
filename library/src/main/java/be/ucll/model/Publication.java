package be.ucll.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Book.class, name = "book"),
        @JsonSubTypes.Type(value = Magazine.class, name = "magazine")
})
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "publication_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required.")
    private String title;

    @Positive(message = "Publication year must be positive.")
    private int publicationYear;

    @PositiveOrZero(message = "Available copies must be a positive number.")
    private int availableCopies;

    // Constructor (unchanged)
    public Publication(String title, int publicationYear, int availableCopies) {
        setTitle(title);
        setPublicationYear(publicationYear);
        setAvailableCopies(availableCopies);
    }

    protected Publication() {}

    // Getters and setters (unchanged)
    public String getTitle() { return title; }
    public int getPublicationYear() { return publicationYear; }
    public int getAvailableCopies() { return availableCopies; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title is required.");
        }
        this.title = title;
    }

    public void setPublicationYear(int publicationYear) {
        if (publicationYear > LocalDate.now().getYear()) {
            throw new RuntimeException("Publication year cannot be in the future.");
        }
        this.publicationYear = publicationYear;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new RuntimeException("Available copies must be a positive number.");
        }
        this.availableCopies = availableCopies;
    }

    // Methods (unchanged)
    public void lendPublication() {
        if (availableCopies <= 0) {
            throw new RuntimeException("No available copies left.");
        }
        availableCopies--;
    }

    public void returnPublication() {
        availableCopies++;
    }
}