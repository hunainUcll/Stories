package be.ucll.model;

public class Magazine extends Publication {
    private String editor;
    private String ISSN;

    public Magazine(String title, String editor, String ISSN, int publicationYear, int availableCopies) {
        super(title, publicationYear, availableCopies);
        setEditor(editor);
        setISSN(ISSN);
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        if (editor == null || editor.trim().isEmpty()) {
            throw new RuntimeException("editor is required.");
        }
        this.editor = editor;
    }

    public String getISSN() {
        return ISSN;
    }

    public void setISSN(String ISSN) {
        if (ISSN == null || ISSN.trim().isEmpty()) {
            throw new RuntimeException("ISSN is required.");
        } else if (!ISSN.matches("^\\d{4}-\\d{4}$")) {
            throw new RuntimeException("ISSN has wrong format");
        }
        this.ISSN = ISSN;
    }
}