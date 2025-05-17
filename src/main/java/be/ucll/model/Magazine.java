package be.ucll.model;

import jakarta.validation.constraints.NotBlank;

public class Magazine extends Publication {

    @NotBlank(message = "editor is required.")
    private String editor;
    @NotBlank(message = "ISSN is required.")
    private String ISSN;

    public Magazine(String title, String editor, String ISSN, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setEditor(editor);
        setISSN(ISSN);
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {this.editor = editor;}

    public String getISSN() {
        return ISSN;
    }

    public void setISSN(String ISSN) {
        if (!ISSN.matches("^\\d{4}-\\d{4}$")) {throw new RuntimeException("ISSN has wrong format");}
        this.ISSN = ISSN;
    }
}