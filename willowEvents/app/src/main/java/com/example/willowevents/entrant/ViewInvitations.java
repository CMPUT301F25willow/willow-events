package com.example.willowevents.entrant;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.InviteArrayAdapter;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.Entrant;

import com.example.willowevents.R;

import java.util.ArrayList;

public class ViewInvitations extends AppCompatActivity {
    ArrayList<Invite> invites;
    ListView inviteListView;
    InviteArrayAdapter inviteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitations);

        invites = new ArrayList<Invite>();
        invites.add(new Invite(new Event("myEventOne"), new Entrant("Myself"), "INVITED"));
        invites.add(new Invite(new Event("myEventTwo"), new Entrant("Myself"), "DECLINED"));
        invites.add(new Invite(new Event("myEventThree"), new Entrant("Myself"), "ACCEPTED"));
        inviteListView = findViewById(R.id.invite_list);
        inviteAdapter = new InviteArrayAdapter(this, invites);
        inviteListView.setAdapter(inviteAdapter);
    }
}