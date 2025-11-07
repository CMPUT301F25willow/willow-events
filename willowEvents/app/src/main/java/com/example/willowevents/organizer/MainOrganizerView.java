package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;
import com.example.willowevents.UserController;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

/**
 * This View displays a list of events that the organizer who is logged in
 * has made, as well as an option to make another. Clicking on an event should
 * take the user to a screen with more options regarding the event, passing the event
 * by giving the eventId as an extra with the tag "Event ID"
 */
public class MainOrganizerView extends AppCompatActivity {
     // shared with EventOrganizerEntrantView
    ListView eventView;
    EventArrayAdapter eventAdapter;
    Button newEventButton;
    Button profileButton;
    public static final String EXTRA_EVENT_ID = "EXTRA_EVENT_ID";
    private ListView eventList;
    private final ArrayList<Event> events = new ArrayList<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_organizer_view);

        eventList = findViewById(R.id.eventList);

        eventAdapter = new EventArrayAdapter(this, events);
        eventList.setAdapter(eventAdapter);

        //TODO: pull list of events with organizerId == deviceID
        //events = ??
        // Open the entrants hub for the tapped event
        eventList.setOnItemClickListener((parent, view, position, id) -> {
            if (position < 0 || position >= events.size()) return;
            String eventId = events.get(position).getId();
            if (eventId == null || eventId.trim().isEmpty()) {
                Toast.makeText(this, "Missing event ID", Toast.LENGTH_SHORT).show();
                return;
            }
            // Navigate to organizer entrants view
            startActivity(
                    new android.content.Intent(this, EventOrganizerEntrantView.class)
                            .putExtra(EXTRA_EVENT_ID, eventId)
            );
        });

        loadMyEvents();
    }

    private void loadMyEvents() {
        // OrganizerId is the device ANDROID_ID in your app
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (deviceId == null || deviceId.trim().isEmpty()) {
            Toast.makeText(this, "Unable to determine organizer ID (ANDROID_ID).", Toast.LENGTH_LONG).show();
            return;
        }

        // One-shot load of events by organizerId.
        // NOTE: whereEqualTo + orderBy may require a Firestore composite index the first time.
        db.collection("events")
                .whereEqualTo("organizerId", deviceId)
                .orderBy("eventDate", Query.Direction.DESCENDING) // newest first
                .get()
                .addOnSuccessListener(qs -> {
                    events.clear();
                    for (DocumentSnapshot d : qs.getDocuments()) {
                        String id = d.getId();
                        String title = d.getString("title");
                        if (title == null || title.trim().isEmpty()) title = "(Untitled Event)";

                        // Create a minimal Event object for the adapter
                        Event e;
                        try {
                            // If your Event has a no-arg constructor, this works:
                            e = new Event();
                            e.setId(id);
                            e.setTitle(title);
                        } catch (Exception ignore) {
                            // If your Event has a (String title) constructor instead, use this:
                            e = new Event(title);
                            // If you have a setId(String) method, set it here:
                            try { e.setId(id); } catch (Exception ignored2) { /* ok if not available */ }
                        }

                        events.add(e);
                    }
                    eventAdapter.notifyDataSetChanged();

                    if (events.isEmpty()) {
                        Toast.makeText(this, "No events yet. Create one to get started.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );

        /*Event event = addMockEvent();
        events.add(event);
        events.add(event);
        events.add(event);
        events.add(event);*/

        // for testing only
        //ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, events);

        // find UI items
        eventView = findViewById(R.id.eventList);
        newEventButton = findViewById(R.id.createNewEvent);
        profileButton = findViewById(R.id.profile_button);

        // set + link array adapter
        eventAdapter = new EventArrayAdapter(this, events);
        eventView.setAdapter(eventAdapter);
        

        newEventButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainOrganizerView.this, EventCreationView.class);
            startActivity(intent);
        });

        eventView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventId = events.get(i).getId();
                Intent myIntent = new Intent(MainOrganizerView.this, EventOrganizerEntrantView.class);
                //need to pass event to be shown
                myIntent.putExtra("Event ID", eventId);
                startActivity(myIntent);

            }
        });

        // TRANSITION TO PROFILE
        profileButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainOrganizerView.this, ProfileView.class);
            startActivity(intent);
        });

    }

}