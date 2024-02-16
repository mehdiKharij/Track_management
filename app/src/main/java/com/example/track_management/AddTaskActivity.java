package com.example.track_management;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.firestore.DocumentReference;


public class AddTaskActivity extends AppCompatActivity {

    private EditText taskTitleEditText;
    private EditText taskDescriptionEditText;
    private EditText taskDeadlineEditText;
    private EditText taskImgEditText;
    private EditText taskDocUriEditText;
    private FloatingActionButton addButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_task_activity);

        taskTitleEditText = findViewById(R.id.task_title_edit_text);
        taskDescriptionEditText = findViewById(R.id.task_description_edit_text);
        taskDeadlineEditText = findViewById(R.id.task_deadline_edit_text);
        taskImgEditText = findViewById(R.id.task_img_edit_text);
        taskDocUriEditText = findViewById(R.id.task_doc_uri_edit_text);
        addButton = findViewById(R.id.fab_add);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTaskToFirestore();
            }
        });
    }

    private void addTaskToFirestore() {
        String taskTitle = taskTitleEditText.getText().toString().trim();
        String taskDescription = taskDescriptionEditText.getText().toString().trim();
        String taskDeadline = taskDeadlineEditText.getText().toString().trim();
        String taskImg = taskImgEditText.getText().toString().trim();
        String taskDocUri = taskDocUriEditText.getText().toString().trim();

        if (!taskDescription.isEmpty()) {
            String userEmail = mAuth.getCurrentUser().getEmail();


            Map<String, Object> task = new HashMap<>();
            task.put("title", taskTitle);
            task.put("description", taskDescription);
            task.put("deadline", taskDeadline);
            task.put("img", taskImg);
            task.put("doc_uri", taskDocUri);

            mFirestore.collection("user").document(userEmail)
                    .collection("tasks")
                    .add(task)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Tâche ajoutée avec succès
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Erreur lors de l'ajout de la tâche
                        }
                    });

        } else {
            // Afficher un message d'erreur si la description de la tâche est vide
        }
    }
}

