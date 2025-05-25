package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@DiscriminatorValue("book")
public class Book extends Publication {
    @NotBlank(message = "Author is required.")
    private String author;

    @NotBlank(message = "ISBN is required.")
    @Pattern(regexp = "\\d{10}|\\d{13}", message = "ISBN must be 10 or 13 digits.")
    private String isbn;
    // Constructor (unchanged)
    @JsonCreator
    public Book(
            @JsonProperty("title") String title,
            @JsonProperty("author") String author,
            @JsonProperty("isbn") String isbn,
            @JsonProperty("publicationYear") int publicationYear,
            @JsonProperty("availableCopies") int availableCopies
    ) {
        super(title, publicationYear, availableCopies);
        setAuthor(author);
        setIsbn(isbn);
    }

    protected Book() {}

    // Getters and setters (unchanged)
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new RuntimeException("Author is required.");
        }
        this.author = author;
    }

    public void setIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new RuntimeException("ISBN is required.");
        }
        if (!isbn.matches("\\d{10}|\\d{13}")) {
            throw new RuntimeException("ISBN must be 10 or 13 digits.");
        }
        this.isbn = isbn;
    }
}