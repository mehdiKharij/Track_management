package com.example.track_management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import model.Note;

public class NoteDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String noteId;
    private String title; // Ajout de la variable title

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        db = FirebaseFirestore.getInstance();

        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        Button deleteButton = findViewById(R.id.deleteButton);

        // Récupérer les données de l'intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("title"); // Stocker le titre dans la variable title
            String description = extras.getString("description");
            noteId = extras.getString("noteId"); // Ajout de la récupération de l'ID de la note

            // Afficher les données dans les TextView correspondants
            titleTextView.setText(title);
            descriptionTextView.setText(description);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
            }
        });
    }

    private void deleteNote() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("user")
                    .document(user.getEmail())
                    .collection("notes")
                    .whereEqualTo("title", title) // Utiliser la variable title pour rechercher la note
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete()
                                        .addOnCompleteListener(deleteTask -> {
                                            if (deleteTask.isSuccessful()) {
                                                Toast.makeText(NoteDetailsActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
                                                finish(); // Retour à l'activité précédente après suppression
                                            } else {
                                                Toast.makeText(NoteDetailsActivity.this, "Failed to delete note", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(NoteDetailsActivity.this, "Failed to find note", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
