package com.example.willowevents.entrant;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.EventController;
import com.example.willowevents.R;
import com.example.willowevents.UserController;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;

/**
 * This view allows Entrant objects to view the details of an Event object
 * and join it's Waiting List
 */
public class EventEntrantView extends AppCompatActivity {


    UserController userController;
    EventController eventController;
    
    String userID;
    Event currentEvent;
    User currentUser;
    Button joinWaitlist;
    Button acceptInvitation;
    Button declineInvitation;
    Button backButton;
    Button leaveWaitlist;

    TextView eventInfo;
    TextView selectionInfo;
    TextView waitlistLength;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        String eventID = extras.getString("eventID");
        // BUTTONS
        joinWaitlist        = findViewById(R.id.join_button);
        acceptInvitation    = findViewById(R.id.accept_button);
        declineInvitation   = findViewById(R.id.decline_button);
        backButton          = findViewById(R.id.back_button);
        leaveWaitlist       = findViewById(R.id.leave_button);

        // TEXT
        eventInfo     = findViewById(R.id.event_info);
        selectionInfo = findViewById(R.id.selection_info);
        waitlistLength = findViewById(R.id.waitlist_length_textview);

        // Get current user ID
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // define controllers
        userController = new UserController();
        eventController = new EventController();

        // GET CURRENT EVENT
        updateCurrentEvent(eventID);

        // get user
        userController.getUser(userID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
            }
        });

        // EDIT INFO
//        editInfo();


        // add join waitlist functionality
        joinWaitlist.setOnClickListener(view -> {
            /// update database directly
            eventController.addUserWaitlist(eventID, userID);
            // update current event
            updateCurrentEvent(eventID);

        });

        // add leave waitlist functionality
        leaveWaitlist.setOnClickListener(view -> {
            /// update database directly
            eventController.removeUserWaitlist(eventID, userID);
            // update current event
            updateCurrentEvent(eventID);

        });

        // add accept invitaiton functionality
        acceptInvitation.setOnClickListener(view -> {
            /// update database directly
            eventController.removeUserInviteList(eventID, userID);
            // REGISTER IN EVENT
            eventController.addUserRegisteredList(eventID, userID);
            // update current event
            updateCurrentEvent(eventID);

        });

        // add decline invitaiton functionality
        declineInvitation.setOnClickListener(view -> {
            // update database on invitation
            eventController.removeUserInviteList(eventID, userID);
            // nothing else left to do once user decclined

            // update current event
            updateCurrentEvent(eventID);

        });




    }

    private String getEventTitle() {
        return currentEvent.getTitle();
    }
    private String getEventDate() {
        return currentEvent.getEventDate().toString();
    }

    private String getSelectionInfo() {
        return currentEvent.getDescription();
    }

    private Integer getWaitlistCount() {
        return currentEvent.getWaitlist().size();
    }

    private Boolean isUserInWaitlist(String userID) {
        return currentEvent.getWaitlist().contains(userID);
    }

    private Boolean isUserInvited(String userID) {
        return currentEvent.getInviteList().contains(userID);
    }

    private Boolean isUserRegistered(String userID) {
        return currentEvent.getApprovedList().contains(userID);
    }

    private Boolean isWaitlistFull() {
        if (currentEvent.getWaitlistLimit() == null) {
            return Boolean.FALSE;
        }
        else{

         return currentEvent.getWaitlist().size() >= currentEvent.getWaitlistLimit();
        }
    }

    private void editInfo() {



        eventInfo.setText(
                getEventTitle() + '|' + getEventDate()
        );

        selectionInfo.setText(getSelectionInfo());

        waitlistLength.setText("Waitlist count: " + getWaitlistCount());



        // for join waitlist button to appear
        // waitlist is not full
        if (!isWaitlistFull() && !isUserRegistered(userID) && !isUserInWaitlist(userID)) {
            // then show join button
            joinWaitlist.setVisibility(Button.VISIBLE);
            leaveWaitlist.setVisibility(Button.INVISIBLE);
        }
        else {
            joinWaitlist.setVisibility(Button.INVISIBLE);
        }

        // if user is already in waitlsit allow user to leave waitlist
        if (isUserInWaitlist(userID)) {
            leaveWaitlist.setVisibility(Button.VISIBLE);
            joinWaitlist.setVisibility(Button.INVISIBLE);
        }

        // accept button to show:
        if (isUserInvited(userID)) {
            acceptInvitation.setVisibility(Button.VISIBLE);
            declineInvitation.setVisibility(Button.VISIBLE);
        }
        else {
            acceptInvitation.setVisibility(Button.INVISIBLE);
            declineInvitation.setVisibility(Button.INVISIBLE);
        }
    }
    private void updateCurrentEvent(String eventID) {
        eventController.generateOneEvent(eventID, new EventController.OnEventGeneration() {
            @Override
            public void onEventGenerated(Event event) {
                currentEvent = event;
                editInfo();
            }
        });
    }




}
