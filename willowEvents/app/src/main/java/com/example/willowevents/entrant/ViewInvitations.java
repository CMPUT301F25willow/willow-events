package com.example.willowevents.entrant;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.Entrant;

import com.example.willowevents.R;

import java.util.ArrayList;

public class ViewInvitations extends AppCompatActivity {
    ArrayList<Invite> invites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitations);

        invites = new ArrayList<Invite>();
        invites.add(new Invite(new Event("myEventOne"), new Entrant("Myself"), "INVITED"));
        invites.add(new Invite(new Event("myEventTwo"), new Entrant("Myself"), "DECLINED"));
        invites.add(new Invite(new Event("myEventThree"), new Entrant("Myself"), "ACCEPTED"));
//        eventView = findViewById(R.id.eventList);
//        eventAdapter = new EventArrayAdapter(this, myEvents);
//        eventView.setAdapter(eventAdapter);
    }
}