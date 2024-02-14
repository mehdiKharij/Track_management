package com.example.track_management;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

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

public class TasksActivity extends AppCompatActivity {

    FirebaseFirestore db;
    LinkedList<Tache> taches;
    RecyclerView myRecycler;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        taches= new  LinkedList<Tache>();

        myRecycler=findViewById(R.id.recycler_tasks);
        getTasks();
    }


    void getTasks(){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference docRef = db.collection("user").document(user.getEmail());
        docRef.collection("tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Tache tache= new Tache(document.getString("title"),document.getString("description"),document.getString("deadline"),document.getString("img"));
                                taches.add(tache);
                            }
                            myRecycler.setHasFixedSize(true);
                            // use a linear layout manager
                            LinearLayoutManager layoutManager = new LinearLayoutManager(TasksActivity.this);
                            myRecycler.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            MyAdapter myAdapter = new MyAdapter(taches,TasksActivity.this);
                            myRecycler.setAdapter(myAdapter);
                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
        /*
        db.collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Tache tache= new Tache(document.getString("title"),document.getString("description"),document.getString("deadline"),document.getString("img"));
                                taches.add(tache);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                            myRecycler.setHasFixedSize(true);
                            // use a linear layout manager
                            LinearLayoutManager layoutManager = new LinearLayoutManager(TasksActivity.this);
                            myRecycler.setLayoutManager(layoutManager);
                            // specify an adapter (see also next example)
                            MyAdapter myAdapter = new MyAdapter(taches,TasksActivity.this);
                            myRecycler.setAdapter(myAdapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

       */

    }
}


/*
DocumentReference docRef = db.collection("user").document(currentUser.getEmail());
        docRef.collection("tasks").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Tache tache= new Tache(document.getString("title"),document.getString("description"),document.getString("deadline"),document.getString("img"));
                                taches.add(tache);
                            }
                        } else {
                            Log.d("not ok", "Error getting documents: ", task.getException());
                        }
                    }
                });
 */