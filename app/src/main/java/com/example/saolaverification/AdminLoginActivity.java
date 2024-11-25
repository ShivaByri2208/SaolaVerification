package com.example.saolaverification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class AdminLoginActivity extends AppCompatActivity {
    private EditText adminmail, adminpaswd;
    private Button adminloginsubmit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView signup ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminlogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Find views
        adminmail = findViewById(R.id.adminemail);
        adminpaswd = findViewById(R.id.adminpaswd);
        adminloginsubmit = findViewById(R.id.adminloginbtn);
        signup = findViewById(R.id.signupfromadmin);
        // Set login button click listener
        adminloginsubmit.setOnClickListener(view -> {
            String email = adminmail.getText().toString().trim();
            String password = adminpaswd.getText().toString().trim();

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(AdminLoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform login
            loginAdmin(email, password);
        });


        signup.setOnClickListener(view -> {
            Intent intent = new Intent(AdminLoginActivity.this , SignUpActivity.class);

            startActivity(intent);
        });
    }



    private void loginAdmin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Check if user is an admin
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            verifyAdminStatus(user.getUid());
                        }
                    } else {
                        // Login failed
                        Toast.makeText(AdminLoginActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyAdminStatus(String userId) {
        db.collection("admins").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // User is an admin, proceed to admin home page
                            Intent intent = new Intent(AdminLoginActivity.this, AdminHomePageActivity.class);
                            startActivity(intent);
                            finish(); // Close the login activity
                        } else {
                            // Not an admin
                            mAuth.signOut();
                            Toast.makeText(AdminLoginActivity.this,
                                    "Access denied. Admin privileges required.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Error checking admin status
                        Toast.makeText(AdminLoginActivity.this,
                                "Error verifying admin status",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}