package com.example.willowevents.initialPages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.provider.Settings;
import android.widget.Toast;


import com.example.willowevents.R;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.model.User;
import com.example.willowevents.organizer.MainOrganizerView;

import java.util.Objects;

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

        // GET DEVICE ID:
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //ADD Firestore stuff to validate
        loginButton.setOnClickListener(view -> {

            // First check if user exists
            userController.userExists(deviceID, new UserController.OnExistsUser() {
                @Override
                public void onExistsUser(boolean userExists, User user) {
                    if (userExists) {
                        // if user is organizer, go to organizer home page
                        if (Objects.equals(user.getUserType(), "organizer")) {
                            Intent myIntent = new Intent(InitialView.this, MainOrganizerView.class);
                            startActivity(myIntent);
                        }
                        // if user is entrant, go to entrant homepage
                        else {
                            Intent myIntent = new Intent(InitialView.this, EntrantHomeView.class);
                            startActivity(myIntent);
                        }
                    }
                    else {
                        String notifyText = "Your device ID is not recognized. Please register your device first.";
                        Toast toast = Toast.makeText(InitialView.this, notifyText, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }
            });
        });

        signupButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(InitialView.this, SelectRoleView.class);
            startActivity(myIntent);
        });

        deviceLoginButton.setOnClickListener(view -> {

            // First check if user exists
            userController.userExists(deviceID, new UserController.OnExistsUser() {
                @Override
                public void onExistsUser(boolean userExists, User user) {
                    if (userExists) {
                        // if user is organizer, go to organizer home page
                        if (Objects.equals(user.getUserType(), "organizer")) {
                            Intent myIntent = new Intent(InitialView.this, MainOrganizerView.class);
                            startActivity(myIntent);
                        }
                        // if user is entrant, go to entrant homepage
                        else {
                            Intent myIntent = new Intent(InitialView.this, EntrantHomeView.class);
                            startActivity(myIntent);
                        }
                    }
                    else {
                        String notifyText = "Your device ID is not recognized. Please register your device first.";
                        Toast toast = Toast.makeText(InitialView.this, notifyText, Toast.LENGTH_SHORT);
                        toast.show();

                    }
                }
            });
        });

        deviceSignupButton.setOnClickListener(view -> {
            // First check if user exists
            userController.userExists(deviceID, new UserController.OnExistsUser() {
                @Override
                public void onExistsUser(boolean userExists, User user) {

                    // valid sign up
                    if (!userExists) {
                        Intent myIntent = new Intent(InitialView.this, SelectRoleView.class);
                        startActivity(myIntent);
                    }
                    // cannot sign up because user exists
                    else {
                        String notifyText = "Your device ID is already registered. Please log in.";
                        Toast toast = Toast.makeText(InitialView.this, notifyText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        });
    }
}