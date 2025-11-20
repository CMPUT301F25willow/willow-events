package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.arrayAdapters.EventArrayAdapter;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

/**
 * This View serves as the main page for a user with entrant permissions. It allows them to.
 * - View event lists       - check event details   - check notifications
 * - filter event lists     - check profile information     - view invitations
 */
public class EntrantHomeView extends AppCompatActivity {
    //Stole from taylor's MainOrganizerView
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> myEvents;
    ArrayList<Event> availableEvents;
    ArrayList<Event> allEvents;
    Button MyEventsButton;
    Button AvailableEventsButton;
    Button AllEventsButton;
    Button ClearFilterButton;
    Button InviteButton;
    ImageView profileIcon;
    EditText FilterOne;
    EditText FilterTwo;
    EditText FilterThree;
    androidx.appcompat.widget.Toolbar FilterBase;
    androidx.appcompat.widget.Toolbar InviteBase;
    Boolean isFilterVisible;
    EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_home);

        // EVENT CONTROLLER
        eventController = new EventController();
        //buttons
        profileIcon = findViewById(R.id.accountIcon);
        MyEventsButton = findViewById(R.id.my_events_button);
        AvailableEventsButton = findViewById(R.id.available_events_button);
        AllEventsButton = findViewById(R.id.all_events_button);
        ClearFilterButton = findViewById(R.id.clear_filter_button);
        //invite elements
        InviteButton = findViewById(R.id.invite_button);
        InviteBase = findViewById(R.id.invite_bar);
        //filter elements
        FilterOne = findViewById(R.id.filter_option_one);
        FilterTwo = findViewById(R.id.filter_option_two);
        FilterThree = findViewById(R.id.filter_option_three);
        FilterBase = findViewById(R.id.filter_bar);
        //Make filter bar disappear
        FilterOne.setVisibility(View.GONE);
        FilterTwo.setVisibility(View.GONE);
        FilterThree.setVisibility(View.GONE);
        FilterBase.setVisibility(View.GONE);
        //Make invite bar disappear
        InviteButton.setVisibility(View.GONE);
        InviteBase.setVisibility(View.GONE);
        isFilterVisible = false;

        eventView = findViewById(R.id.eventList);
        // TODO: add user ID checking here along with events to get the events the user is signed up in
        myEvents = new ArrayList<Event>();



        // TODO: add in Event controller logic to handle dates
        availableEvents = new ArrayList<Event>();

        // Switch to invitations view so user can see their invitations
        InviteButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EntrantHomeView.this, ViewNotifications.class);
            startActivity(myIntent);
        });

        // Toggle visibility of filter elements when filter button is clicked
        ClearFilterButton.setOnClickListener(view -> {
            if(isFilterVisible){
                FilterOne.setVisibility(View.GONE);
                FilterTwo.setVisibility(View.GONE);
                FilterThree.setVisibility(View.GONE);
                FilterBase.setVisibility(View.GONE);
                isFilterVisible = false;
            }
            else {
                FilterOne.setVisibility(View.VISIBLE);
                FilterTwo.setVisibility(View.VISIBLE);
                FilterThree.setVisibility(View.VISIBLE);
                FilterBase.setVisibility(View.VISIBLE);
                isFilterVisible = true;
            }
        });

        //Display the users events
        MyEventsButton.setOnClickListener(view -> {
            //TODO: I don't think my events is actually interfacing with firebase
            eventView = findViewById(R.id.eventList);
            eventAdapter = new EventArrayAdapter(this, myEvents);
            eventView.setAdapter(eventAdapter);
            //Make invite bar appear
            InviteButton.setVisibility(View.VISIBLE);
            InviteBase.setVisibility(View.VISIBLE);
        });

        //Display all events in the system
        AllEventsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);
            // GET ALL EVENTS FROM FIRESTORE
            allEvents = new ArrayList<>();
            eventAdapter = new EventArrayAdapter(this, myEvents);
            eventView.setAdapter(eventAdapter);
            // Get all events
            eventController.generateAllEvents(new EventController.OnEventsGeneration() {
                @Override
                public void onEventsGenerated(ArrayList<Event> events) {
                    allEvents=events;
                    eventAdapter = new EventArrayAdapter(EntrantHomeView.this, allEvents);
                    eventView.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                }
            });


            //Make invite bar disappear
            InviteButton.setVisibility(View.GONE);
            InviteBase.setVisibility(View.GONE);
        });

        //Display all events still accepting entrants in the system
        AvailableEventsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);
            eventAdapter = new EventArrayAdapter(this, availableEvents);
            eventView.setAdapter(eventAdapter);
            //Make invite bar disappear
            InviteButton.setVisibility(View.GONE);
            InviteBase.setVisibility(View.GONE);
        });

        // detect if a user clicks an event in the event list and go to
        // the corresponding event details page EventEntrantView
        eventView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = (Event )parent.getItemAtPosition(position);
                    Intent myIntent = new Intent(EntrantHomeView.this, EventEntrantView.class);
                    myIntent.putExtra("eventID", selectedEvent.getId());
                    startActivity(myIntent);
                });

        // Transition to profile page
        profileIcon.setOnClickListener(view -> {
            Intent intent = new Intent(EntrantHomeView.this, ProfileView.class);
            startActivity(intent);
            }


        );
    }
}