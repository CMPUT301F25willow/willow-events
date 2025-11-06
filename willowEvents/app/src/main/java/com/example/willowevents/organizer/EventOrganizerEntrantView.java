package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.model.Event;

/**
 * This View shows the organizer options regarding the waitlist and
 * other lists of users in the event.
 */
public class EventOrganizerEntrantView extends AppCompatActivity {

    private String eventId;
    private Event event;
    private Button seeWaitlist;
    private Button seeInvited;
    private Button seeEnrolled;
    private Button seeCancelled;
    private Button seeInfo;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_entrants_view);

        Intent origIntent = new Intent(this, MainOrganizerView.class);
        //check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
        } else {
            origIntent = new Intent(this, EventOrganizerInfoView.class);
            extras = getIntent().getExtras();
            if (extras != null) {
                eventId = extras.getString("Event ID");
            }
        }
        if (eventId != null) {
            //TODO: event = firestore find event via eventId (then uncomment else exception below)
            TextView eventName = findViewById(R.id.eventName);
            eventName.setText(event.getName());
        } else {
//            throw new IllegalArgumentException("event ID not acquired");
        }

        backButton = findViewById(R.id.back_button);
        seeInfo = findViewById(R.id.info_button);
        seeWaitlist = findViewById(R.id.waitlist_see_entrants_button);
        seeInvited = findViewById(R.id.invited_see_entrants_button);
        seeEnrolled = findViewById(R.id.enrolled_see_entrants_button);
        seeCancelled = findViewById(R.id.cancelled_see_entrants_button);

        seeInfo.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, EventOrganizerInfoView.class);
            myIntent.putExtra("event ID", eventId);
            startActivity(myIntent);
        });

        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });

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