package com.example.track_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button addTrackButton = findViewById(R.id.add_track_button);
        Button listTracksButton = findViewById(R.id.list_tracks_button);
        Button logoutButton = findViewById(R.id.logout_button);

        addTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirection vers l'activité AddTrackActivity
                startActivity(new Intent(HomeActivity.this, AddTrackActivity.class));
            }
        });


        listTracksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajoutez votre code ici pour gérer le clic sur le bouton d'affichage de la liste des pistes
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Déconnexion de Firebase
                FirebaseAuth.getInstance().signOut();

                // Redirection vers l'écran de connexion
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish(); // Fermer l'activité HomeActivity
            }
        });

    }
}
