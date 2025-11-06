package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.organizer.MainOrganizerView;

public class SelectRoleView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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
            // PRINT FALLBACK STATEMENT
            String notifyText = "Admin feature not yet implemented.";
            Toast toast = Toast.makeText(SelectRoleView.this, notifyText, Toast.LENGTH_SHORT);
            toast.show();
        });

    }




}
