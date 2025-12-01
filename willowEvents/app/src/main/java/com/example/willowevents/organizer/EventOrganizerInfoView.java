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
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This View shows event information from the organizer's perspective,
 * with options to update information or change settings.
 */
public class EventOrganizerInfoView extends AppCompatActivity {
    private String eventId;
    private Event event;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_info_view);

        Button backButton = findViewById(R.id.back_button);
        Button seeEntrants = findViewById(R.id.entrants_button);
        Button qrButton = findViewById(R.id.qr_code_button);
        TextView eventName = findViewById(R.id.eventName);


        Intent origIntent = new Intent(this, EventOrganizerEntrantView.class);
//check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
            if (eventId == null) {
                android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
                startActivity(origIntent);
            }
        } else {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            startActivity(origIntent);
        }
        if (eventId == null) {
            return;
        }
        db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    event = doc.toObject(Event.class);

                    if (event == null) {
                        android.widget.Toast.makeText(
                                EventOrganizerInfoView.this,
                                "Could not find event data",
                                android.widget.Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    // Fill in UI with event data
                    if (eventName != null) {
                        eventName.setText(event.getTitle());
                    }

                    //set up QR button
                    qrButton.setOnClickListener(v -> {
                        Intent intent = new Intent(EventOrganizerInfoView.this, QrCodeActivity.class);
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_NAME, event.getTitle());
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_DESCRIPTION, event.getDescription());
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_POSTER_URL, event.getPosterUrl());  // 👈 NEW
                        startActivity(intent);
                    });
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(
                            EventOrganizerInfoView.this,
                            "Failed to load event: " + e.getMessage(),
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                });


        //TODO: event = get event from database with eventId

        seeEntrants.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });


        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });
    }
}