package be.ucll.model;

import java.util.Calendar;
import java.time.LocalDate;

public abstract class Publication {
    private String title;
    private int publicationYear;
    private int availableCopies;

    public Publication(String title, int publicationYear, int availableCopies) {
        setTitle(title);
        setPublicationYear(publicationYear);
        setAvailableCopies(availableCopies);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("title is required.");
        }
        this.title = title;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        int currentYear = LocalDate.now().getYear();
        if (publicationYear <= 0) {
            throw new RuntimeException("Publication year must be a positive integer.");
        } else if (publicationYear > currentYear) {
            throw new RuntimeException("Publication year cannot be in the future.");
        }
        this.publicationYear = publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        if (availableCopies < 0) {
            throw new RuntimeException("Available copies must be a positive number.");
        }
        this.availableCopies = availableCopies;
    }

    public void lendPublications(){
        if (availableCopies < 0){
            throw new RuntimeException("No more copies available");
        }
        this.availableCopies --;
    }

    public void returnPublication() {
        availableCopies++; // Increment available copies on return
    }


}