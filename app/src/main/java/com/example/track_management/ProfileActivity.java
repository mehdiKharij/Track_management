package com.example.track_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileActivity extends AppCompatActivity {

    private TextView firstNameTextView, lastNameTextView, telTextView;
    private FirebaseFirestore db;

    private ImageView menuImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseFirestore.getInstance();

        firstNameTextView = findViewById(R.id.firstNameTextView);
        lastNameTextView = findViewById(R.id.lastNameTextView);
        telTextView = findViewById(R.id.telTextView);

        // Récupérer l'utilisateur actuellement connecté
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Récupérer les informations de l'utilisateur à partir de Firestore
            db.collection("user")
                    .document(user.getEmail())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String firstName = documentSnapshot.getString("firstName");
                            String lastName = documentSnapshot.getString("lastName");
                            String tel = documentSnapshot.getString("tel");

                            // Afficher les informations de l'utilisateur dans les TextView correspondants
                            firstNameTextView.setText(firstName);
                            lastNameTextView.setText(lastName);
                            telTextView.setText(tel);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Gérer les erreurs de récupération des informations de l'utilisateur
                    });
        }
        menuImage = findViewById(R.id.menuImage);

        menuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
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
                        startActivity(new Intent(ProfileActivity.this, TasksActivity.class));
                        return true;
                    case 2:
                        startActivity(new Intent(ProfileActivity.this, NotesActivity.class));
                        return true;
                    case 3:
                        startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                        return true;
                    case 4:
                        Toast.makeText(ProfileActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
}
