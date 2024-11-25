package com.example.saolaverification;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button signUpButton;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signupactivity), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.signupemail);
        passwordInput = findViewById(R.id.signuppasswd);
        confirmPasswordInput = findViewById(R.id.signupconfirmpasswd);
        signUpButton = findViewById(R.id.signupbtn);

        // Set up click listener for signup button
        signUpButton.setOnClickListener(view -> handleSignUp());
    }

    private void handleSignUp() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.endsWith(".edu") && !email.contains("@")) {
            Toast.makeText(this, "Please enter a valid institutional email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User created successfully, now add to admins collection
                        String userId = mAuth.getCurrentUser().getUid();
                        createAdminDocument(userId, email);
                    } else {
                        // If sign up fails, display a message to the user
                        Toast.makeText(SignUpActivity.this,
                                "Sign up failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createAdminDocument(String userId, String email) {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("email", email);
        adminData.put("createdAt", System.currentTimeMillis());
        adminData.put("isAdmin", true);

        db.collection("admins").document(userId)
                .set(adminData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignUpActivity.this,
                            "Admin account created successfully",
                            Toast.LENGTH_SHORT).show();
                    // Redirect to admin home page
                    Intent intent = new Intent(SignUpActivity.this, AdminHomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // If creating admin document fails, delete the auth user
                    mAuth.getCurrentUser().delete();
                    Toast.makeText(SignUpActivity.this,
                            "Failed to create admin account: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}