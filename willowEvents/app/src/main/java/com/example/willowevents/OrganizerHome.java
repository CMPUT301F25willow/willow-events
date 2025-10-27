package com.example.willowevents;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class OrganizerHome extends AppCompatActivity {
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_home);

        events = new ArrayList<Event>();
//      REPLACE WITH FIRESTORE SHIT:
//        events.add(new Event("eventOne"));
//        events.add(new Event("eventTwo"));
//        events.add(new Event("eventThree"));
//        events.add(new Event("eventFour"));
//        events.add(new Event("eventFive"));

        eventView = findViewById(R.id.eventList);
        eventAdapter = new EventArrayAdapter(this, events);
        eventView.setAdapter(eventAdapter);

    }
}