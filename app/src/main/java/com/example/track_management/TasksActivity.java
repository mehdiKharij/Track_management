package com.example.track_management;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.LinkedList;

import model.Tache;

public class TasksActivity extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    FirebaseFirestore db;
    LinkedList<Tache> taches;
    RecyclerView myRecycler;
    private FirebaseAuth mAuth;
    private ImageView menuImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        taches = new LinkedList<>();

        myRecycler = findViewById(R.id.recycler_tasks);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add); // Récupérer le bouton fab_add

        // Ajouter un écouteur de clic pour le bouton fab_add
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirection vers AddTrackActivity
                Intent intent = new Intent(TasksActivity.this, AddTaskActivity.class);
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
        getTasks();
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
                        startActivity(new Intent(TasksActivity.this, TasksActivity.class));
                        return true;
                    case 2:
                        startActivity(new Intent(TasksActivity.this, NotesActivity.class));
                        return true;
                    case 3:
                        startActivity(new Intent(TasksActivity.this, ProfileActivity.class));
                        return true;
                    case 4:
                        Toast.makeText(TasksActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

    void getTasks(){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(user.getEmail());
        docRef.collection("tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            taches.clear(); // Clear existing tasks
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Tache tache= new Tache(document.getString("id") ,document.getString("title") ,document.getString("description"),document.getString("deadline"),document.getString("img"));
                                taches.add(tache);
                            }
                            myRecycler.setHasFixedSize(true);
                            // use a linear layout manager
                            LinearLayoutManager layoutManager = new LinearLayoutManager(TasksActivity.this);
                            myRecycler.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            MyAdapter myAdapter = new MyAdapter(taches, TasksActivity.this, TasksActivity.this);
                            myRecycler.setAdapter(myAdapter);
                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // Méthode pour gérer les clics sur les éléments de la liste
    @Override
    public void onItemClick(Tache tache) {
        // Ouvrir TaskDetailsActivity avec les détails de la tâche sélectionnée
        Intent intent = new Intent(TasksActivity.this, TaskDetailsActivity.class);
        intent.putExtra("id", tache.getId());
        intent.putExtra("title", tache.getTitle());
        intent.putExtra("description", tache.getDescription());
        intent.putExtra("deadline", tache.getDeadline());
        intent.putExtra("img", tache.getImg());
        // Start TaskDetailsActivity with request code 1
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Refresh the task list after deletion
                getTasks();
            }
        }
    }
}
