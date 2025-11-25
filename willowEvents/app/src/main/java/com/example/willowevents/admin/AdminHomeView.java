package com.example.willowevents.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.arrayAdapters.EventArrayAdapter;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.arrayAdapters.NotificationArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.arrayAdapters.UserArrayAdapter;
import com.example.willowevents.entrant.EventEntrantView;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.User;

import java.util.ArrayList;

public class AdminHomeView extends AppCompatActivity {

    ListView eventView;
    EventArrayAdapter eventAdapter;
    UserArrayAdapter userAdapter;
//    ImageArrayAdapter imageAdapter;
    NotificationArrayAdapter notifAdapter;
    ArrayList<Event> eventList;
    ArrayList<User> profileList;
//    ArrayList<Images> imageList; //TODO create image class to reference ?
    ArrayList<Invite> notifList; //TODO make sure compatible with Vivian's Notification class
    Button BrowseEvents;
    Button BrowseProfiles;
    Button BrowseImages;
    Button BrowseNotifs;
    Button deleteButton;
    ImageView profileIcon;
    EventController eventController;
    Event selectedEvent;
    User selectedProfile;
//    Image selectedImage;
//    Notif selectedNotif;

    enum IsDisplaying {
        EVENT,
        PROFILE,
        IMAGE,
        NOTIF,
        NULL
    }
    IsDisplaying isDisplaying = IsDisplaying.NULL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_view);
        // EVENT CONTROLLER
        eventController = new EventController();
        //buttons
        profileIcon = findViewById(R.id.accountIcon);
        BrowseEvents = findViewById(R.id.browse_events_button);
        BrowseProfiles = findViewById(R.id.browse_profiles_button);
        BrowseImages = findViewById(R.id.browse_images_button);
        BrowseNotifs = findViewById(R.id.browse_notifs_button);
        deleteButton = findViewById(R.id.delete_button);
        deleteButton.setVisibility(View.GONE);

        eventView = findViewById(R.id.eventList);
        eventList = new ArrayList<>();
        profileList = new ArrayList<>();
//        imageList = new ArrayList<>();
        notifList = new ArrayList<>();

        //Display all system events
        BrowseEvents.setOnClickListener(view -> {
            //TODO: display all events in the database
            eventView = findViewById(R.id.eventList);
            eventAdapter = new EventArrayAdapter(this, eventList);
            eventView.setAdapter(eventAdapter);

            IsDisplaying isDisplaying = IsDisplaying.EVENT;
        });

        //Display all system users
        BrowseProfiles.setOnClickListener(view -> {
            //TODO: display all users in the database
            eventView = findViewById(R.id.eventList);
            userAdapter = new UserArrayAdapter(this, profileList);
            eventView.setAdapter(userAdapter);

            IsDisplaying isDisplaying = IsDisplaying.PROFILE;
        });

        //Display all system images
//        BrowseImages.setOnClickListener(view -> {
//            //TODO: display all images in the database
//            eventView = findViewById(R.id.eventList);
//            imageAdapter = new ImageArrayAdapter(this, imageList);
//            eventView.setAdapter(imageAdapter);

//            IsDisplaying isDisplaying = IsDisplaying.IMAGE;
//        });

        //Display all system notifications
        BrowseNotifs.setOnClickListener(view -> {
            //TODO: display all invites/notifs in the database
            eventView = findViewById(R.id.eventList);
//            notifAdapter = new NotificationArrayAdapter(this, notifList);
//            eventView.setAdapter(notifAdapter);

            IsDisplaying isDisplaying = IsDisplaying.NOTIF;
        });

        //TODO: Modify to also support profiles, images, notifs
        eventView.setOnItemClickListener((parent, view, position, id) -> {
            switch(isDisplaying){
                case EVENT:
                    selectedEvent = (Event)parent.getItemAtPosition(position);
                    deleteButton.setVisibility(View.VISIBLE);
                    break;
                case PROFILE:
                    selectedProfile = (User)parent.getItemAtPosition(position);
                    deleteButton.setVisibility(View.VISIBLE);
                    break;
//                case IMAGE:
//                    Image selectedImage = (Image)parent.getItemAtPosition(position);
//                    deleteButton.setVisibility(View.GONE);
//                    break;
//                case NOTIF:
//                    Notification selectedNotifs = (Notification)parent.getItemAtPosition(position);
//                    deleteButton.setVisibility(View.GONE);
//                    break;
            }
        });

        deleteButton.setOnClickListener(view -> {
            switch(isDisplaying){
                case EVENT:
                    // TODO remove selectedEvent from events
                    deleteButton.setVisibility(View.GONE);
                    break;
                case PROFILE:
                    // TODO remove selectedProfile from events
                    deleteButton.setVisibility(View.GONE);
                    break;
//                case IMAGE:
                    // TODO remove selectedImage from events
//                    deleteButton.setVisibility(View.GONE);
//                    break;
//                case NOTIF:
                    // TODO remove selectedNotif from events
//                deleteButton.setVisibility(View.GONE);
//                    break;
            }

        });

        // Transition to profile page
        profileIcon.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminHomeView.this, ProfileView.class);
                    startActivity(intent);
                }


        );
    }
}