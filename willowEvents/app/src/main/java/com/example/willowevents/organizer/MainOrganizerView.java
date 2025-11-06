package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;

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
        //events.add(new Event("eventOne"));
        //events.add(new Event("eventTwo"));
        // events.add(new Event("eventThree"));
        // events.add(new Event("eventFour"));
        // events.add(new Event("eventFive"));

        Event event = addMockEvent();
        events.add(event);
        events.add(event);
        events.add(event);
        events.add(event);

        // for testing only
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, events);

        eventView = findViewById(R.id.eventList);   //find event ListView
        //eventAdapter = new EventArrayAdapter(this, events); //set array adapter
        //eventView.setAdapter(eventAdapter);     //link array adapter to ListView

        eventView.setAdapter(arrayAdapter);


        eventView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(MainOrganizerView.this, EventOrganizerEntrantView.class);
                //share data with new activity
                myIntent.putExtra("Key", 10);
                startActivity(myIntent);

            }


        });


    }


    public Event addMockEvent() {
        Event event = new Event();
        event.setTitle("EventOne");
        event.setDescription("A mock event for testing.");
        event.setOrganizerId("organizer-123");

        // Add some mock user IDs to the waitlist
        ArrayList<String> waitlistIds = new ArrayList<>();
        waitlistIds.add("user-001");
        waitlistIds.add("user-002");
        waitlistIds.add("user-003");
        waitlistIds.add("user-004");

        event.setWaitlist(waitlistIds);

        return null;

    }
}
