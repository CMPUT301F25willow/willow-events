package com.example.willowevents;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainOrganizerView extends AppCompatActivity {
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_organizer_view);

        events = new ArrayList<Event>();
//      REPLACE WITH FIRESTORE SHIT:
        events.add(new Event("eventOne"));
        events.add(new Event("eventTwo"));
        events.add(new Event("eventThree"));
        events.add(new Event("eventFour"));
        events.add(new Event("eventFive"));


        eventView = findViewById(R.id.eventList);   //find event ListView
        eventAdapter = new EventArrayAdapter(this, events); //set array adapter
        eventView.setAdapter(eventAdapter);     //link array adapter to ListView

    }
}