package be.ucll.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@DiscriminatorValue("book")
public class Book extends Publication {

    @NotBlank(message = "author is required.")
    private String author;
    @NotBlank(message = "ISBN is required.")
    @Pattern(
            regexp = "^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d$",
            message = "ISBN has wrong format"
    )
    private String ISBN;

    public Book(String title, String author, String ISBN, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setAuthor(author);
        setISBN(ISBN);
     }

    protected Book() {}

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {this.author = author;}

    public String getISBN() {return ISBN;}
    public void setISBN(String ISBN) {this.ISBN = ISBN;}
}