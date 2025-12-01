package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.FilterEventsDialog;
import com.example.willowevents.arrayAdapters.EventArrayAdapter;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.initialPages.InitialView;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This View allows interactivity for an Entrant object
 * - View event lists       - check event details   - check notifications
 * - filter event lists     - check profile information     - view invitations
 */
public class EntrantHomeView extends AppCompatActivity implements FilterEventsDialog.FilterListener {
    //Stole from taylor's MainOrganizerView
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> myEvents;
    ArrayList<Event> availableEvents;
    ArrayList<Event> allEvents;
    ArrayList<Event> filteredEvents;
    Button MyEventsButton;
    Button AvailableEventsButton;
    Button AllEventsButton;
    Button ClearFilterButton;
    Button InviteButton;
    ImageView profileIcon;
    ImageView filterIcon;
    EditText FilterOne;
    EditText FilterTwo;
    EditText FilterThree;
    TextView userNameText;
    androidx.appcompat.widget.Toolbar FilterBase;
    androidx.appcompat.widget.Toolbar InviteBase;
    Boolean isFilterVisible;
    EventController eventController;

    String userID;

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
        filterIcon = findViewById(R.id.filterIcon);
        userNameText = findViewById(R.id.UserNameText);

        //invite elements
        InviteButton = findViewById(R.id.notification_button);
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
        myEvents = new ArrayList<Event>();


        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        UserController userController = new UserController();
        userController.getUser(userID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                User currentUser = user;
                // VIEWS AND INTERACTIBLES
                userNameText.setText(currentUser.getName());
            }
        });
        //uuhhhhh I did the logic based on if the inviteList exceeds the capacity


        // Switch to invitations view so user can see their invitations
        InviteButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EntrantHomeView.this, ViewNotifications.class);

            // get user
            userController.getUser(userID, new UserController.OnUserLoaded() {
                @Override
                public void onUserLoaded(User user) {

                    if (user.isHasNotifsMuted()) {
                        Toast toast = Toast.makeText(EntrantHomeView.this, "Notifications are muted", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        startActivity(myIntent);
                    }

                }
            });

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

            myEvents = new ArrayList<>();
            eventController.generateAllEvents(new EventController.OnEventsGeneration() {
                @Override
                public void onEventsGenerated(ArrayList<Event> events) {
                    for (Event event: events) {

                        // check if user is approved or on waitlist for the event
                        List<String> waitlist = event.getWaitlist();
                        List<String> registered = event.getApprovedList();
                        List<String> inviteList = event.getInviteList();

                        // show event if user is on waitlist or already approved
                        if (waitlist.contains(userID) || registered.contains(userID) || inviteList.contains(userID)) {
                            myEvents.add(event);
                        }
                    }
                    // now set the event adapter
                    eventAdapter = new EventArrayAdapter(EntrantHomeView.this, myEvents);
                    eventView.setAdapter(eventAdapter);

                }
            });
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

        // FILTER ICON FOR USER AVAILABILITY + PREFERENCES
        filterIcon.setOnClickListener(view -> {
                new FilterEventsDialog().show(getSupportFragmentManager(), "filter");
        });

        //detect if a user clicks an event in the event list and go to
        //
        eventView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = (Event )parent.getItemAtPosition(position);
                    Intent myIntent = new Intent(EntrantHomeView.this, EventEntrantView.class);

                    if (selectedEvent.getId() == null) {
                        String notifyText = "ERROR: Cannot open event. Event has no ID.";
                        Toast toast = Toast.makeText(EntrantHomeView.this, notifyText, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {

                        myIntent.putExtra("eventID", selectedEvent.getId());
                        startActivity(myIntent);
                    }
                });


        profileIcon.setOnClickListener(view -> {
            // TRANSITION TO PROFILE PAGE
            Intent intent = new Intent(EntrantHomeView.this, ProfileView.class);
            startActivity(intent);
            }


        );
    }

    @Override
    public void onFilterConfirmation(List<String> preferences, Date from, Date to) {

        // check if dates are consistent
        boolean datesConsistent = true;
        if (to != null && from != null) {

            // FLAG IF DATES ARE NOT CONSISTENT
            if (to.before(from)) {
                String notifyText = "FROM date must NOT be after TO date.";
                Toast toast = Toast.makeText(EntrantHomeView.this, notifyText, Toast.LENGTH_SHORT);
                toast.show();
                datesConsistent = false;
            }
        }

        if (datesConsistent) {
            eventController.filterEvents(preferences, from, to, new EventController.OnEventsGeneration() {
                @Override
                public void onEventsGenerated(ArrayList<Event> events) {
                    // now that events have been generated show them in adapter
                    filteredEvents = events;
                    eventAdapter = new EventArrayAdapter(EntrantHomeView.this, filteredEvents);
                    eventView.setAdapter(eventAdapter);
                    eventAdapter.notifyDataSetChanged();
                }
            });
        }

    }


}
