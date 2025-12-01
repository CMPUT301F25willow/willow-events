package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.arrayAdapters.NotificationArrayAdapter;
import com.example.willowevents.ProfileView;
import com.example.willowevents.controller.NotificationController;
import com.example.willowevents.model.Event;

import com.example.willowevents.R;
import com.example.willowevents.model.Notification;

import java.util.ArrayList;

/**
 * This View allows entrants to see their invitations and accept,
 * withdraw or decline as they see fit
 */
public class ViewNotifications extends AppCompatActivity {
    ArrayList<Notification> notis;
    ListView notiListView;
    NotificationArrayAdapter notiAdapter;
    Button backButton;

    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notifications);

        backButton = findViewById(R.id.back_button);
        notiListView = findViewById(R.id.invite_list);

        // get userID
        userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // NOTIFICATION CONTROLLER
        NotificationController notificationController = new NotificationController();

        // RETRIEVE NOTIFICATIONS
        notificationController.generateUserNotifications(userID, new NotificationController.OnNotificationsGenerated() {
            @Override
            public void onNotificationsLoaded(ArrayList<Notification> notis) {
                notiAdapter = new NotificationArrayAdapter(ViewNotifications.this, notis);
                notiListView.setAdapter(notiAdapter);

            }
        });

        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(ViewNotifications.this, ProfileView.class);
            startActivity(myIntent);
        });

        notiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String eventId = notis.get(i).getEventId();
                Intent myIntent = new Intent(ViewNotifications.this, EventEntrantView.class);
                //need to pass event to be shown
                myIntent.putExtra("eventID", eventId);
                startActivity(myIntent);
            }
        });
    }
}