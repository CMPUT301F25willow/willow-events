package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.EventController;
import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.UserController;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;
import com.example.willowevents.organizer.MainOrganizerView;

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

        // go back
        backButton.setOnClickListener( view -> {
            Intent myIntent = new Intent(EventEntrantView.this, EntrantHomeView.class);
            startActivity(myIntent);
        }
        );






    }

    /**
     * This returns a String title from object currentEvent
     * @return title
     */
    private String getEventTitle() {
        return currentEvent.getTitle();
    }

    /**
     * This returns a Date eventDate in String format from object currentEvent
     * @return String eventDate
     */
    private String getEventDate() {
        return currentEvent.getEventDate().toString();
    }

    /**
     * This returns a String description + String lotteryDetails from object currentEvent
     * @return description +  lotteryDetails
     */
    private String getSelectionInfo() {
        return currentEvent.getDescription()+currentEvent.getLotteryDetails();
    }

    /**
     * This returns an Integer size of an arraylist waitlist from object currentEvent
     * @return Integer size of arraylist
     */
    private Integer getWaitlistCount() {
        return currentEvent.getWaitlist().size();
    }

    /**
     * This uses a parameter String userID to return a boolean if userID
     * is in the arrayList waitlist of object current.event
     * @param userID    - String
     * @return  boolean
     */
    private Boolean isUserInWaitlist(String userID) {
        return currentEvent.getWaitlist().contains(userID);
    }

    /**
     * This uses a parameter String userID to return a boolean if userID
     * is in the arrayList inviteList of object current.event
     * @param userID    - String
     * @return  boolean
     */
    private Boolean isUserInvited(String userID) {
        return currentEvent.getInviteList().contains(userID);
    }
    /**
     * This uses a parameter String userID to return a boolean if userID
     * is in the arrayList approvedList of object current.event
     * @param userID    - String
     * @return  boolean
     */
    private Boolean isUserRegistered(String userID) {
        return currentEvent.getApprovedList().contains(userID);
    }

    /**
     * This returns a boolean based on the size of an object currentEvent's
     * arrayList waitList and the Integer value of waitListLimit
     * @return  boolean
     */
    private Boolean isWaitlistFull() {
        if (currentEvent.getWaitlistLimit() == null) {
            return Boolean.FALSE;
        }
        else{

         return currentEvent.getWaitlist().size() >= currentEvent.getWaitlistLimit();
        }
    }

    /**
     * This updates the view based on what list from object currentEvent this entrant is in
     * and the data from currentEvent object
     */
    private void editInfo() {



        eventInfo.setText(
                getEventTitle() + '|' + getEventDate()
        );

        selectionInfo.setText(getSelectionInfo());
        selectionInfo.setMovementMethod(new ScrollingMovementMethod());

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

    /**
     * Using the currentEvent object's String eventID, update the view based on currentEvent
     * @param eventID   - String
     */
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
