package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.organizer.MainOrganizerView;

public class SelectRoleView extends AppCompatActivity {

    Button entrantButton;
    Button organizerButton;
    Button adminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        entrantButton = findViewById(R.id.entrant_button);
        organizerButton = findViewById(R.id.organizer_button);
        adminButton = findViewById(R.id.admin_button);
        //FIRESTORE ADD THIS INFO TO THE USER PROFILE SOMEHOW
        entrantButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(SelectRoleView.this, EntrantHomeView.class);
            startActivity(myIntent);
        });
        organizerButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(SelectRoleView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });
    }
}