package be.ucll.model;

public class Book extends Publication {
    private String author;
    private String ISBN;

    public Book(String title, String author, String ISBN, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setAuthor(author);
        setISBN(ISBN);
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            throw new RuntimeException("author is required.");
        }
        this.author = author;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        if (ISBN == null || ISBN.trim().isEmpty()) {
            throw new RuntimeException("ISBN is required.");
        } else if (!ISBN.matches("^\\d{3}-\\d{1,5}-\\d{1,7}-\\d{1,7}-\\d$")) {
            throw new RuntimeException("ISBN has wrong format");
        }
        this.ISBN = ISBN;
    }
}