package com.example.saolaverification;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class SaolaVerificationApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Firebase
        FirebaseApp.initializeApp(this);
    }
}