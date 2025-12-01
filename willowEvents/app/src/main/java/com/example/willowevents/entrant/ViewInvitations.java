package com.example.willowevents.entrant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.arrayAdapters.InviteArrayAdapter;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.Entrant;

import com.example.willowevents.R;

import java.util.ArrayList;

/**
 * This View allows entrants to see their invitations and accept,
 * withdraw or decline as they see fit
 */
public class ViewInvitations extends AppCompatActivity {
    ArrayList<Invite> invites;
    ListView inviteListView;
    InviteArrayAdapter inviteAdapter;
    Button backButton;
    Button acceptWaitlistButton;
    Button declineWithdrawButton;
    Invite curInvite;

    //GOING TO NEED FIRESTORE HELP WITH THIS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invitations);
        //find all buttons on the page
        backButton = findViewById(R.id.back_button);
        acceptWaitlistButton = findViewById(R.id.accept_waitlist_button);
        declineWithdrawButton = findViewById(R.id.decline_withdraw_button);
        // If you cant find curInvite dont allow the user to click any buttons
        //TODO why is the only definition of curInvite commented out? Why is that definition an onclick listener?
        if(curInvite == null){
            acceptWaitlistButton.setVisibility(View.GONE);
            declineWithdrawButton.setVisibility(View.GONE);
        }
        // Toggle button visibility and names so only names corresponding to current
        // status of invite are visible and enabled
        else if (!(curInvite == null)){
            switch(curInvite.getStatus()){
                case("INVITED"):
                    acceptWaitlistButton.setText("Accept");
                    declineWithdrawButton.setText("Decline");
                case("ACCEPTED"):
                    acceptWaitlistButton.setVisibility(View.GONE);
                    declineWithdrawButton.setText("Withdraw");
                case("DECLINED"):
                    acceptWaitlistButton.setVisibility(View.GONE);
                    declineWithdrawButton.setVisibility(View.GONE);
                case("NOT INVITED"):
                    acceptWaitlistButton.setText("Waitlist");
                    declineWithdrawButton.setVisibility(View.GONE);
            }
        }

//        inviteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                curInvite = invites.get(i);
//            }
//        });

        //allow user to go back to EntrantHomeView when they are done looking at their invitations
        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(ViewInvitations.this, EntrantHomeView.class);
            startActivity(myIntent);
        });

        // If accept/waitlist button has been clicked update the status and
        // database info to match
        acceptWaitlistButton.setOnClickListener(view -> {
            if (!(curInvite == null)){
                switch(curInvite.getStatus()){
                    case("INVITED"):
                        curInvite.setStatus("ACCEPTED");
                        //TODO add firestore
                        //update status
                        //update event lists to match
                        //update buttons
                        acceptWaitlistButton.setVisibility(View.GONE);
                        declineWithdrawButton.setText("Withdraw");
                    case("NOT INVITED"):
                        curInvite.setStatus("NOT INVITED");
                        //TODO add firestore
                        //update event lists (they signed up to be on waitlist)
                }
            }
        });

        // If decline/withdraw button has been clicked update the status and
        // database info to match
        declineWithdrawButton.setOnClickListener(view -> {
            if (!(curInvite == null)){
                switch(curInvite.getStatus()){
                    case("INVITED"):
                        curInvite.setStatus("DECLINED");
                        //TODO add firestore
                        //update status
                        //update event lists to match
                        //update buttons
                        acceptWaitlistButton.setVisibility(View.GONE);
                        declineWithdrawButton.setVisibility(View.GONE);
                    case("ACCEPTED"):
                        curInvite.setStatus("DECLINED");
                        //TODO add firestore
                        //update status
                        //update event lists to match
                        //update buttons
                        acceptWaitlistButton.setVisibility(View.GONE);
                        declineWithdrawButton.setVisibility(View.GONE);
                }
            }
        });

        //Initialize a temp list for dev manual testing
        invites = new ArrayList<Invite>();
        invites.add(new Invite(new Event("myEventOne"), new Entrant("Myself"), "INVITED"));
        invites.add(new Invite(new Event("myEventTwo"), new Entrant("Myself"), "DECLINED"));
        invites.add(new Invite(new Event("myEventThree"), new Entrant("Myself"), "ACCEPTED"));
        inviteListView = findViewById(R.id.invite_list);
        inviteAdapter = new InviteArrayAdapter(this, invites);
        inviteListView.setAdapter(inviteAdapter);
    }
}