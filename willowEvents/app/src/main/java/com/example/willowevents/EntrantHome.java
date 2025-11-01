package com.example.willowevents;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EntrantHome extends AppCompatActivity {
    String currentEntrantID;
    User currentEntrant ;

    UserController userController ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        userController = new UserController();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


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


    }
}