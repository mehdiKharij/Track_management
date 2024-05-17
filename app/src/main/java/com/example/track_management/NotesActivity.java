package com.example.track_management;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import model.Note;

public class NotesActivity extends AppCompatActivity implements MyAdapterNotes.OnItemClickListener {

    FirebaseFirestore db;
    LinkedList<Note> notes;
    RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private ImageView menuImage;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        notes = new LinkedList<>();

        recyclerView = findViewById(R.id.recycler_view_notes);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
                startActivity(intent);
            }
        });

        menuImage = findViewById(R.id.menuImage);

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getNotes();
    }

    private void showMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenu().add(0, 1, 0, "Tasks");
        popupMenu.getMenu().add(0, 2, 0, "Notes");
        popupMenu.getMenu().add(0, 3, 0, "Profile");
        popupMenu.getMenu().add(0, 4, 0, "Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        startActivity(new Intent(NotesActivity.this, TasksActivity.class));
                        return true;
                    case 2:
                        startActivity(new Intent(NotesActivity.this, NotesActivity.class));
                        return true;
                    case 3:
                        startActivity(new Intent(NotesActivity.this, ProfileActivity.class));
                        return true;
                    case 4:
                        Toast.makeText(NotesActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    void getNotes(){
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("user").document(user.getEmail());
            docRef.collection("notes").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                notes.clear(); // Clear existing notes
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Note note = document.toObject(Note.class);
                                    notes.add(note);
                                }
                                recyclerView.setHasFixedSize(true);
                                LinearLayoutManager layoutManager = new LinearLayoutManager(NotesActivity.this);
                                recyclerView.setLayoutManager(layoutManager);
                                MyAdapterNotes adapter = new MyAdapterNotes(notes, NotesActivity.this, NotesActivity.this);
                                recyclerView.setAdapter(adapter);
                            } else {
                                Log.d("NotesActivity", "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }


    @Override
    public void onItemClick(Note note) {
        Intent intent = new Intent(NotesActivity.this, NoteDetailsActivity.class);
        intent.putExtra("title", note.getTitle());
        intent.putExtra("description", note.getDescription());
        startActivity(intent);
    }

}
