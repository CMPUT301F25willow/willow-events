package com.example.willowevents.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.EventController;
import com.example.willowevents.InviteArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.UserArrayAdapter;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.entrant.EventEntrantView;
import com.example.willowevents.entrant.ViewInvitations;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.User;

import java.util.ArrayList;

public class AdminHomeView extends AppCompatActivity {

    ListView eventView;
    EventArrayAdapter eventAdapter;
    UserArrayAdapter userAdapter;
//    ImageArrayAdapter imageAdapter;
    InviteArrayAdapter notifAdapter;
    ArrayList<Event> eventList;
    ArrayList<User> profileList;
//    ArrayList<Images> imageList; //TODO create image class to reference ?
    ArrayList<Invite> notifList;
    Button BrowseEvents;
    Button BrowseProfiles;
    Button BrowseImages;
    Button BrowseNotifs;
    ImageView profileIcon;
    EventController eventController;

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
        });

        //Display all system users
        BrowseProfiles.setOnClickListener(view -> {
            //TODO: display all users in the database
            eventView = findViewById(R.id.eventList);
            userAdapter = new UserArrayAdapter(this, profileList);
            eventView.setAdapter(userAdapter);
        });

        //Display all system images
//        BrowseImages.setOnClickListener(view -> {
//            //TODO: display all images in the database
//            eventView = findViewById(R.id.eventList);
//            imageAdapter = new ImageArrayAdapter(this, imageList);
//            eventView.setAdapter(imageAdapter);
//        });

        //Display all system notifications
        BrowseImages.setOnClickListener(view -> {
            //TODO: display all invites/notifs in the database
            eventView = findViewById(R.id.eventList);
            notifAdapter = new InviteArrayAdapter(this, notifList);
            eventView.setAdapter(notifAdapter);
        });

        //TODO: Modify to also support profiles, images, notifs
        eventView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = (Event )parent.getItemAtPosition(position);
            Intent myIntent = new Intent(AdminHomeView.this, EventEntrantView.class);
            myIntent.putExtra("eventID", selectedEvent.getId());
            startActivity(myIntent);
        });

        // Transition to profile page
        profileIcon.setOnClickListener(view -> {
                    Intent intent = new Intent(AdminHomeView.this, ProfileView.class);
                    startActivity(intent);
                }


        );
    }
}