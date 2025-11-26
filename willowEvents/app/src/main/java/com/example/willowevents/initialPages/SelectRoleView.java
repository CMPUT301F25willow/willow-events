package com.example.willowevents.initialPages;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.R;
import com.example.willowevents.admin.AdminHomeView;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.organizer.MainOrganizerView;

/**
 * This View requests a user to choose a role/Object class their phone
 * will be registered as and effects the interactivity of the app
 */
public class SelectRoleView extends AppCompatActivity {

    Button entrantButton;
    Button organizerButton;
    Button adminButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button chooseEntrant = findViewById(R.id.entrant_button);
        Button chooseOrganizer = findViewById(R.id.organizer_button);
        Button chooseAdmin = findViewById(R.id.admin_button);

        // DEVICE ID
        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // CONNECT CONTROLLER
        UserController userController = new UserController();


        // IF USER SELECTS ENTRANT
        chooseEntrant.setOnClickListener(view -> {
            // ADD NEW ENTRANT
            userController.addNewEntrantUser(deviceID);
            // GO TO ENTRANT HOME
            Intent myIntent = new Intent(SelectRoleView.this, EntrantHomeView.class);
            startActivity(myIntent);
        });


        // IF USER SELECTS ORGANIZER
        chooseOrganizer.setOnClickListener(view -> {
            // ADD NEW ORGANIZER
            userController.addNewOrganizerUser(deviceID);
            // GO TO ORGANIZER HOME
            Intent myIntent = new Intent(SelectRoleView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });
        // IF USER SELECTS ADMIN
        chooseAdmin.setOnClickListener(view -> {
            // TODO: ADD NEW ADMIN
            // userController.addNewOrganizerUser(deviceID);
            // GO TO ORGANIZER HOME
            Intent myIntent = new Intent(SelectRoleView.this, AdminHomeView.class);
            startActivity(myIntent);
        });

    }
}