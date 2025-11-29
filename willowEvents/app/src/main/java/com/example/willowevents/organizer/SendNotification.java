package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Notification;

/**
 *
 */
public class SendNotification extends AppCompatActivity {

    private String eventId;
    private String listType;
    private Button cancelButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification);

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

            Event event; //find event with eventId to create notification with

            //TODO: use eventId to grab event from database (replace next line)
            event = new Event();

            //create notification and success popup
            // TODO: grab list from database with eventId and listType to iterate through entrants in list
            // HERE ARE THE LIST TYPES:
            // - cancelledList
            // - enrolledList
            // - invitedList
            // - waitList

            // for (entrant in list) new Notification(event, notificationMessageTextbox.getText().toString(), entrantId);

            android.widget.Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show();

            //Switch views
            Intent myIntent = new Intent(SendNotification.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

    }

}
