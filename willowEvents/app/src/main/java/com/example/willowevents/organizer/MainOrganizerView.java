package com.example.willowevents.organizer;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.R;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

/**
 * This View displays a list of events that the organizer who is logged in
 * has made, as well as an option to make another. Clicking on an event should
 * take the user to a screen with more options regarding the event.
 */
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