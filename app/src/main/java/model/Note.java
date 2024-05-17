package model;

import com.google.firebase.firestore.DocumentId;

public class Note {
    @DocumentId
    private String id;
    private String title;
    private String description;

    public Note() {
        // Constructeur par défaut requis pour Firestore
    }

    public Note(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Ajoutez des getters et des setters si nécessaire
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
