package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class InitialView extends AppCompatActivity {
    String currentEntrantID;
    User currentEntrant ;
    Button loginButton;
    Button signupButton;
    Button deviceLoginButton;
    Button deviceSignupButton;

    UserController userController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userController = new UserController();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_initial_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);
        deviceLoginButton = findViewById(R.id.device_login_button);
        deviceSignupButton = findViewById(R.id.device_signup_button);

        //ADD Firestore stuff to validate

        loginButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(InitialView.this, EntrantHomeView.class);
            startActivity(myIntent);
        });

        signupButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(InitialView.this, SelectRoleView.class);
            startActivity(myIntent);
        });

        deviceLoginButton.setOnClickListener(view -> {
            // get current entrant from database
            userController.getUser("REPLACE WITH USER ID HERE", new UserController.OnUserLoaded() {
                @Override
                public void onUserLoaded(User user) {
                    currentEntrant = user;
                    if (user == null) {
                        // perhaps raise exception here
                        throw new IllegalArgumentException("Invalid ID provided");
                    }
                }
            });
            Intent myIntent = new Intent(InitialView.this, EntrantHomeView.class);
            startActivity(myIntent);
        });

        deviceSignupButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(InitialView.this, SelectRoleView.class);
            startActivity(myIntent);
        });
    }
}