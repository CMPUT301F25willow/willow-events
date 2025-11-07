package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.EventController;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

/**
 * This View allows interactivity for an Entrant object
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



//        //buttons
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
        //uuhhhhh I did the logic based on if the inviteList exceeds the capacity


        InviteButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EntrantHomeView.this, ViewInvitations.class);
            startActivity(myIntent);
        });

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

        MyEventsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);
            eventAdapter = new EventArrayAdapter(this, myEvents);
            eventView.setAdapter(eventAdapter);
            //Make invite bar appear
            InviteButton.setVisibility(View.VISIBLE);
            InviteBase.setVisibility(View.VISIBLE);
        });

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

        AvailableEventsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);

            // Start with an empty list bound to the ListView (same pattern as AllEventsButton)
            availableEvents = new ArrayList<>();
            eventAdapter = new EventArrayAdapter(this, availableEvents);
            eventView.setAdapter(eventAdapter);

            // Fetch all events, then filter to "available" and display
            eventController.generateAllEvents(new EventController.OnEventsGeneration() {
                @Override
                public void onEventsGenerated(ArrayList<Event> events) {
                    // Keep a copy of all events if you need to switch tabs later
                    allEvents = (events != null) ? events : new ArrayList<>();

                    // Apply your availability rule from the controller
                    availableEvents = new ArrayList<>(eventController.filterAvailable(allEvents));

                    // Bind the filtered list
                    eventAdapter = new EventArrayAdapter(EntrantHomeView.this, availableEvents);
                    eventView.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                }
            });

            // Hide invite bar for this view
            InviteButton.setVisibility(View.GONE);
            InviteBase.setVisibility(View.GONE);
        });


        eventView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = (Event )parent.getItemAtPosition(position);
                    Intent myIntent = new Intent(EntrantHomeView.this, EventEntrantView.class);
                    myIntent.putExtra("eventID", selectedEvent.getId());
                    startActivity(myIntent);
                });


        profileIcon.setOnClickListener(view -> {
            // TRANSITION TO PROFILE PAGE
            Intent intent = new Intent(EntrantHomeView.this, ProfileView.class);
            startActivity(intent);
            }


        );
    }
}