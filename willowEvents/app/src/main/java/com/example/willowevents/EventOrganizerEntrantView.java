package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EventOrganizerEntrantView extends AppCompatActivity {

    private Button seeWaitlist;
    private Button seeInvited;
    private Button seeEnrolled;
    private Button seeCancelled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_entrants_view);

        seeWaitlist = findViewById(R.id.waitlist_see_entrants_button);
        seeInvited = findViewById(R.id.invited_see_entrants_button);
        seeEnrolled = findViewById(R.id.enrolled_see_entrants_button);
        seeCancelled = findViewById(R.id.cancelled_see_entrants_button);

        seeWaitlist.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "waitlist");
            startActivity(myIntent);
        });

        seeInvited.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "invited");
            startActivity(myIntent);
        });

        seeEnrolled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "enrolled");
            startActivity(myIntent);
        });

        seeCancelled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "cancelled");
            startActivity(myIntent);
        });
    }
}