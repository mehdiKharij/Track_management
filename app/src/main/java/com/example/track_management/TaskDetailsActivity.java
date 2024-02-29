package com.example.track_management;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        // Récupérer les données passées depuis l'intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String deadline = getIntent().getStringExtra("deadline");
        String img = getIntent().getStringExtra("img");

        // Afficher les données dans les vues correspondantes
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView deadlineTextView = findViewById(R.id.deadlineTextView);
        ImageView imageView = findViewById(R.id.imageView);

        titleTextView.setText(title);
        descriptionTextView.setText(description);
        deadlineTextView.setText(deadline);

        // Charger l'image depuis Firebase Storage avec Glide
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img);
        Glide.with(this /* context */)
                .load(storageReference)
                .into(imageView);
    }
}
