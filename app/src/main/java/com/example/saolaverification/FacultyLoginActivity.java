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

public class FacultyLoginActivity extends AppCompatActivity {
    private EditText facultyEmailEditText;
    private EditText facultyPasswordEditText;
    private Button facultyLoginButton;
    private FirebaseAuth mAuth;
    TextView signupfromfaculty ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_faculty_login);

        // Set up window insets (same as AdminLoginActivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.facultylogin), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Find views -  Corrected IDs
        facultyEmailEditText = findViewById(R.id.facultyemail);
        facultyPasswordEditText = findViewById(R.id.facultypaswd);
        facultyLoginButton = findViewById(R.id.facultyloginbtn); // Corrected ID
        signupfromfaculty = findViewById(R.id.signupfromfaculty);
        // Set login button click listener
        facultyLoginButton.setOnClickListener(v -> {
            String email = facultyEmailEditText.getText().toString().trim();
            String password = facultyPasswordEditText.getText().toString().trim();

            // Validate input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(FacultyLoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email domain
            if (!email.endsWith("@vnrvjiet.in")) {
                Toast.makeText(FacultyLoginActivity.this, "Invalid email domain. Use @vnrvjiet.in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform login (replace with your actual login logic)
            loginFaculty(email, password);
        });

        signupfromfaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FacultyLoginActivity.this , SignUpActivity.class);
                startActivity(intent);
            }
        });
    }



    private void loginFaculty(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful, navigate to the next activity
                        Intent intent = new Intent(FacultyLoginActivity.this, FacultyHomePageActivity.class); // Replace with your home activity
                        startActivity(intent);
                        finish(); // Close the login activity
                    } else {
                        // Login failed
                        Toast.makeText(FacultyLoginActivity.this,
                                "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}