package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;
import com.example.willowevents.UserController;

import java.util.ArrayList;

/**
 * This View displays a list of events that the organizer who is logged in
 * has made, as well as an option to make another. Clicking on an event should
 * take the user to a screen with more options regarding the event, passing the event
 * by giving the eventId as an extra with the tag "Event ID"
 */
public class MainOrganizerView extends AppCompatActivity {
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> events;
    Button newEventButton;
    Button profileButton;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_organizer_view);

        String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        events = new ArrayList<Event>();
        //TODO: pull list of events with organizerId == deviceID
        //events = ??

        /*Event event = addMockEvent();
        events.add(event);
        events.add(event);
        events.add(event);
        events.add(event);*/

        // for testing only
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, events);

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


//    public Event addMockEvent(){
//
//        Event event = new Event("EventOne");
//        ArrayList<String> tempList = new ArrayList<>();
//        ArrayList<Entrant> entrantlist = new ArrayList<>();
//
//        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
//        entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
//        entrantlist.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
//        entrantlist.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));
//
//        event.setWaitlist(entrantlist);
//        return event;
//    }



}