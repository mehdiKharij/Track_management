package com.example.track_management;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddTrackActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText mTitleField, mDescriptionField, mStartDateField, mEndDateField;
    private Button mAddTrackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_track_activity);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mTitleField = findViewById(R.id.title_field);
        mDescriptionField = findViewById(R.id.description_field);
        mStartDateField = findViewById(R.id.start_date_field);
        mEndDateField = findViewById(R.id.end_date_field);
        mAddTrackButton = findViewById(R.id.add_track_button);

        mAddTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTrack();
            }
        });
    }

    private void addTrack() {
        String title = mTitleField.getText().toString().trim();
        String description = mDescriptionField.getText().toString().trim();
        String startDate = mStartDateField.getText().toString().trim();
        String endDate = mEndDateField.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user email
        String userEmail = mAuth.getCurrentUser().getEmail();

        // Create a new track map
        Map<String, Object> track = new HashMap<>();
        track.put("title", title);
        track.put("description", description);
        track.put("start_date", startDate);
        track.put("end_date", endDate);

        // Add the track to Firestore
        db.collection("tracks")
                .document(userEmail) // Use email as document ID
                .set(track)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddTrackActivity.this, "Track added successfully", Toast.LENGTH_SHORT).show();
                        // Clear fields after adding the track
                        mTitleField.setText("");
                        mDescriptionField.setText("");
                        mStartDateField.setText("");
                        mEndDateField.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddTrackActivity.this, "Failed to add track: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
