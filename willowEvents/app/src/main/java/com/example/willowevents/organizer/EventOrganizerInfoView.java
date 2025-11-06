package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.R;
import com.example.willowevents.model.Event;

/**
 * This View shows event information from the organizer's perspective,
 * with options to update information or change settings.
 */
public class EventOrganizerInfoView extends AppCompatActivity {
    private String eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_info_view);

        Button backButton = findViewById(R.id.back_button);
        Button seeEntrants = findViewById(R.id.entrants_button);

        Intent origIntent = new Intent(this, EventOrganizerEntrantView.class);
        //check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
            if (eventId != null) {
                //TODO: event = firestore find event via eventId (then uncomment else exception below)
                TextView eventName = findViewById(R.id.eventName);
                //Other initializations

                eventName.setText(event.getName());
                //Other sets
            } else {
//                throw new IllegalArgumentException("event ID not acquired");
            }
        }


        seeEntrants.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("event ID", eventId);
            startActivity(myIntent);
        });

        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });
    }
}