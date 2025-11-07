package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.model.Event;
import com.example.willowevents.organizer.MainOrganizerView;

import java.util.ArrayList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrant_home);

        // get current user from DB



        //



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
        //      REPLACE WITH FIRESTORE SHIT:
        myEvents = new ArrayList<Event>();
        myEvents.add(new Event("myEventOne"));
        myEvents.add(new Event("myEventTwo"));
        myEvents.add(new Event("myEventThree"));
        myEvents.add(new Event("myEventFour"));
        myEvents.add(new Event("myEventFive"));
        //      REPLACE WITH FIRESTORE SHIT:
        allEvents = new ArrayList<Event>();
        allEvents.add(new Event("allEventOne"));
        allEvents.add(new Event("allEventTwo"));
        allEvents.add(new Event("allEventThree"));
        allEvents.add(new Event("allEventFour"));
        allEvents.add(new Event("allEventFive"));
        //      REPLACE WITH FIRESTORE SHIT:
        availableEvents = new ArrayList<Event>();
        availableEvents.add(new Event("avEventOne"));
        availableEvents.add(new Event("avEventTwo"));
        availableEvents.add(new Event("avEventThree"));
        availableEvents.add(new Event("avEventFour"));
        availableEvents.add(new Event("avEventFive"));

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
            eventAdapter = new EventArrayAdapter(this, allEvents);
            eventView.setAdapter(eventAdapter);
            //Make invite bar disappear
            InviteButton.setVisibility(View.GONE);
            InviteBase.setVisibility(View.GONE);
        });

        AvailableEventsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);
            eventAdapter = new EventArrayAdapter(this, availableEvents);
            eventView.setAdapter(eventAdapter);
            //Make invite bar disappear
            InviteButton.setVisibility(View.GONE);
            InviteBase.setVisibility(View.GONE);
        });

        profileIcon.setOnClickListener(view -> {
            // TRANSITION TO PROFILE PAGE
            Intent intent = new Intent(EntrantHomeView.this, ProfileView.class);
            startActivity(intent);
            }


        );
    }
}