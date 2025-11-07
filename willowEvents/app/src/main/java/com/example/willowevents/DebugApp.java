package com.example.willowevents;

import android.app.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DebugApp extends Application {
    @Override public void onCreate() {
        super.onCreate();
        // Point the SDKs to local emulators for *every* Debug run (app & tests)
        FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080);
        FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
    }
}

