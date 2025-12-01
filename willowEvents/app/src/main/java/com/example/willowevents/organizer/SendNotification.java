package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.controller.NotificationController;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Notification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SendNotification extends AppCompatActivity {

    private String eventId;
    private String listType;
    private Button cancelButton;
    private Button sendButton;
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification);

        // Event Controller
        EventController eventController = new EventController();
        NotificationController notificationController = new NotificationController();


        //check for data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
            listType = extras.getString("List type");
        } else {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // make sure we have eventId to make notifications with
        if (eventId == null || eventId.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // make sure that we have listType to make notifications with
        else if (listType == null || listType.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);

        titleText = findViewById(R.id.title_text);
        titleText.setText("To: " + listType);

        // goes back to EventOrganizerEntrantView
        cancelButton.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(SendNotification.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        // creates notification, creates success popup and goes back to EventOrganizerEntrantView
        sendButton.setOnClickListener(view -> {
            EditText notificationMessageTextbox = findViewById(R.id.notification_message);

            // QUERY EVENT
            eventController.generateOneEvent(eventId, new EventController.OnEventGeneration() {
                @Override
                public void onEventGenerated(Event event) {
                    //create notification and success popup
                    // HERE ARE THE LIST TYPES:
                    // - cancelledList
                    // - enrolledList
                    // - invitedList
                    // - waitList


                    // GET THE LIST FOR THE EVENT
                    // arraylist of the user IDs for notification sending
                    List<String> userIDs = new ArrayList<>();
                    if (listType.equals("cancelledList")) {
                        userIDs = event.getCancelledList();
                    } else if (listType.equals("enrolledList")) {
                        userIDs = event.getApprovedList();
                    }  else if (listType.equals("invitedList")) {
                        userIDs = event.getInviteList();
                    }  else if (listType.equals("waitList")) {
                        userIDs = event.getWaitlist();
                    }

                    // ITERATE THROUGH USER IDS THEN CREATE NOTIFICATIONS TO DATABASE
                    for (String userID: userIDs) {
                        Notification notification = new Notification(event, notificationMessageTextbox.getText().toString(), userID);
                        // PLACE NOTIFICATION IN DATABASE
                        notificationController.addNotification(notification);
                    }


                    // NOTIFICATIONS HAVE BEEN SEND AND TRANSITION WILL HAPPEN
                    android.widget.Toast.makeText(SendNotification.this, "Notification sent!", Toast.LENGTH_SHORT).show();
                    //Switch views
                    Intent myIntent = new Intent(SendNotification.this, EventOrganizerEntrantView.class);
                    myIntent.putExtra("Event ID", eventId);
                    startActivity(myIntent);
                }
            });



            // for (entrant in list) new Notification(event, notificationMessageTextbox.getText().toString(), entrantId);



        });

    }

}
