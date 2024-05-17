package com.example.track_management;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ImageView menuImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        popupMenu.getMenu().add(0, 3, 0, "Logout");
        popupMenu.getMenu().add(0, 4, 0, "Profile");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1:
                        startActivity(new Intent(MainActivity.this, TasksActivity.class));
                        return true;
                    case 2:
                        startActivity(new Intent(MainActivity.this, NotesActivity.class));
                        return true;
                    case 3:
                        Toast.makeText(MainActivity.this, "Logout clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case 4:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
}
