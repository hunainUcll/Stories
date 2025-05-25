package be.ucll.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("magazine")
public class Magazine extends Publication {
    @NotBlank(message = "Editor is required.")
    private String editor;

    @NotBlank(message = "ISSN is required.")
    private String issn;
    // Constructor (unchanged)
    @JsonCreator
    public Magazine(
            @JsonProperty("title") String title,
            @JsonProperty("editor") String editor,
            @JsonProperty("issn") String issn,
            @JsonProperty("publicationYear") int publicationYear,
            @JsonProperty("availableCopies") int availableCopies
    ) {
        super(title, publicationYear, availableCopies);
        setEditor(editor);
        setIssn(issn);
    }

    protected Magazine() {}

    // Getters and setters (unchanged)
    public String getEditor() { return editor; }
    public String getIssn() { return issn; }

    public void setEditor(String editor) {
        if (editor == null || editor.trim().isEmpty()) {
            throw new RuntimeException("Editor is required.");
        }
        this.editor = editor;
    }

    public void setIssn(String issn) {
        if (issn == null || issn.trim().isEmpty()) {
            throw new RuntimeException("ISSN is required.");
        }
        this.issn = issn;
    }
}