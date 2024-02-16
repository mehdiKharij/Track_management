// SignUpActivity.java
package com.example.track_management;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText mEmailField, mPasswordField, mFirstNameField, mLastNameField, mTelField;
    private Button mSignUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mEmailField = findViewById(R.id.signup_email);
        mPasswordField = findViewById(R.id.signup_password);
        mFirstNameField = findViewById(R.id.signup_first_name);
        mLastNameField = findViewById(R.id.signup_last_name);
        mTelField = findViewById(R.id.signup_tel);
        mSignUpButton = findViewById(R.id.signup_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        // Ajoutez cette partie de code ici
        TextView loginRedirectText = findViewById(R.id.login_redirect_text);
        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirection vers LoginActivity
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }


    private void signUp() {
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String firstName = mFirstNameField.getText().toString();
        String lastName = mLastNameField.getText().toString();
        String tel = mTelField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) || TextUtils.isEmpty(tel)) {
            Toast.makeText(SignUpActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = email; // Utiliser l'e-mail comme ID utilisateur
                            saveUserDataToFirestore(userId, firstName, lastName, tel);
                            Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "User creation failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserDataToFirestore(String email, String firstName, String lastName, String tel) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        userData.put("tel", tel);

        db.collection("user").document(email) // Utiliser l'e-mail comme ID du document
                .set(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("SignUpActivity", "User data added to Firestore successfully");
                        } else {
                            Log.w("SignUpActivity", "Error adding user data to Firestore", task.getException());
                        }
                    }
                });
    }



}
