package com.example.track_management;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import androidx.annotation.Nullable;



import model.Tache;

public class TaskDetailsActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private static final int UPDATE_TASK_REQUEST_CODE = 1;

    private static final String TAG = "TaskDetailsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        db = FirebaseFirestore.getInstance();

        // Retrieve task data from intent and create Tache object
        String taskId = getIntent().getStringExtra("id"); // Utilisez "id" pour extraire l'identifiant de la tâche
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String deadline = getIntent().getStringExtra("deadline");
        String img = getIntent().getStringExtra("img");
        Tache tache = new Tache(taskId, title, description, deadline, img);

        // Display task data in corresponding views
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView deadlineTextView = findViewById(R.id.deadlineTextView);
        ImageView imageView = findViewById(R.id.imageView);
        Button deleteButton = findViewById(R.id.deleteButton);

        titleTextView.setText(tache.getTitle());
        descriptionTextView.setText(tache.getDescription());
        deadlineTextView.setText(tache.getDeadline());

        // Load image from Firebase Storage using Glide
        Glide.with(this)
                .load(tache.getImg())
                .into(imageView);

        // Set click listener for edit button
        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskDetailsActivity.this, UpdateTaskActivity.class);
                intent.putExtra("id", tache.getId());
                intent.putExtra("title", tache.getTitle());
                intent.putExtra("description", tache.getDescription());
                intent.putExtra("deadline", tache.getDeadline());
                intent.putExtra("img", tache.getImg());
                startActivityForResult(intent, UPDATE_TASK_REQUEST_CODE);
            }
        });

        // Set click listener for delete button
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log taskId for debugging
                Log.d(TAG, "Task ID: " + taskId);
                deleteTaskFromFirestore(user, title);
            }
        });
    }

    private void deleteTaskFromFirestore(FirebaseUser user, String title) {
        if (title != null && !title.isEmpty()) {
            // Search for task documents with the given title
            db.collection("user").document(user.getEmail())
                    .collection("tasks")
                    .whereEqualTo("title", title)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // Check if any documents were found
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // Iterate through each document and delete it
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete()
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Error occurred while deleting task
                                                    Log.e(TAG, "Error deleting task", e);
                                                }
                                            });
                                }
                                // Notify user that tasks were deleted successfully
                                Toast.makeText(TaskDetailsActivity.this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK); // Set result to indicate success
                                finish(); // Finish the activity after deleting the task
                            } else {
                                // No task found with the specified title
                                Toast.makeText(TaskDetailsActivity.this, "No tasks found with the specified title", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error occurred while searching for tasks
                            Log.e(TAG, "Error searching for tasks", e);
                            Toast.makeText(TaskDetailsActivity.this, "Failed to delete tasks", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Title is null or empty
            Toast.makeText(TaskDetailsActivity.this, "Task title is null or empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_TASK_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Update task details based on the result from UpdateTaskActivity
            String updatedTitle = data.getStringExtra("title");
            String updatedDescription = data.getStringExtra("description");
            String updatedDeadline = data.getStringExtra("deadline");
            String updatedImg = data.getStringExtra("img");

            // Update views with the updated task details
            TextView titleTextView = findViewById(R.id.titleTextView);
            TextView descriptionTextView = findViewById(R.id.descriptionTextView);
            TextView deadlineTextView = findViewById(R.id.deadlineTextView);
            ImageView imageView = findViewById(R.id.imageView);

            titleTextView.setText(updatedTitle);
            descriptionTextView.setText(updatedDescription);
            deadlineTextView.setText(updatedDeadline);
            Glide.with(this).load(updatedImg).into(imageView);

            // Show a toast message indicating successful update
            Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
