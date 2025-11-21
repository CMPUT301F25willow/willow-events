package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Notification;

public class SendNotification extends AppCompatActivity {

    private String eventId;
    private String listType;
    private Button cancelButton;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_notification);

        Intent origIntent = new Intent(this, EventOrganizerEntrantView.class);
        //check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
            listType = extras.getString("List type");
        } else {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (eventId == null || eventId.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        } else if (listType == null || listType.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        cancelButton = findViewById(R.id.cancel_button);
        sendButton = findViewById(R.id.send_button);

        cancelButton.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(SendNotification.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("event ID", eventId);
            startActivity(myIntent);
        });

        sendButton.setOnClickListener(view -> {
            EditText notificationMessageTextbox = findViewById(R.id.notification_message);

            Event event;

            //TODO: use eventId to grab event from database (replace next line)
            event = new Event();

            new Notification(event, notificationMessageTextbox.getText().toString(), listType);

            //Switch views
            Intent myIntent = new Intent(SendNotification.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("event ID", eventId);
            startActivity(myIntent);
        });

    }

}
