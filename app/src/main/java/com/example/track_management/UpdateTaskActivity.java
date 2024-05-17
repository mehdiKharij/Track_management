package com.example.track_management;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.Map;

public class UpdateTaskActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, deadlineEditText, imgEditText;
    private Button saveButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        deadlineEditText = findViewById(R.id.deadlineEditText);
        imgEditText = findViewById(R.id.imageUrl);
        saveButton = findViewById(R.id.saveButton);






        // Récupérer les données de la tâche transmises depuis TaskActivity
        taskId = getIntent().getStringExtra("taskId");
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String deadline = getIntent().getStringExtra("deadline");
        String img = getIntent().getStringExtra("img");

        // Pré-remplir les champs avec les données existantes de la tâche
        titleEditText.setText(title);
        descriptionEditText.setText(description);
        deadlineEditText.setText(deadline);
        imgEditText.setText(img);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTask();
            }
        });
    }

    private void updateTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String deadline = deadlineEditText.getText().toString().trim();
        String img = imgEditText.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description) || TextUtils.isEmpty(deadline) || TextUtils.isEmpty(img)) {
            Toast.makeText(UpdateTaskActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getEmail();

            // Recherche de la tâche par son titre dans Firestore
            db.collection("user").document(userId).collection("tasks")
                    .whereEqualTo("title", title)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // Mise à jour du document avec les nouvelles données de la tâche
                                    DocumentReference taskRef = document.getReference();
                                    taskRef.update("description", description,
                                                    "deadline", deadline,
                                                    "img", img)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> updateTask) {
                                                    if (updateTask.isSuccessful()) {
                                                        Toast.makeText(UpdateTaskActivity.this, "Task mis à jour avec succès", Toast.LENGTH_SHORT).show();
                                                        finish(); // Retour à l'activité précédente (TaskActivity)
                                                    } else {
                                                        Toast.makeText(UpdateTaskActivity.this, "Erreur lors de la mise à jour de la tâche", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(UpdateTaskActivity.this, "Erreur lors de la recherche de la tâche", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(UpdateTaskActivity.this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }
    }

}
