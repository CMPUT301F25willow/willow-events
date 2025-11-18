package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Lottery;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Random;

/**
 * This View shows the organizer options regarding the waitlist and
 * other lists of users in the event.
 */
public class EventOrganizerEntrantView extends AppCompatActivity {

    private String eventId;
    private Event event;
    private Button seeWaitlist;
    private Button seeInvited;
    private Button seeEnrolled;
    private Button seeCancelled;
    private Button notifyWaitlist;
    private Button notifyInvited;
    private Button notifyEnrolled;
    private Button notifyCancelled;
    private Button seeInfo;
    private Button backButton;
    private Button sendInvite;
    private Button updateEvent;
    private boolean redraw = false;

    private String eventID;
    private final Lottery lottery = new Lottery();    // refer to Lottery.java in model folder

    private final com.google.firebase.firestore.FirebaseFirestore db =
            com.google.firebase.firestore.FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_entrants_view);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

//        int invitedNum = event.getInviteList().size() + event.getApprovedList().size();
//        int limitNum = event.getInvitelistlimit();
//
//        if(redraw){
//            if (invitedNum < limitNum) {
//                sendInvite.setText("Draw replacements");
//            } else {
//                sendInvite.setText("Invite Limit Reached");
//                sendInvite.setEnabled(false);
//            }
//        }

        //check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        eventId = extras.getString("Event ID");
        if (eventId == null || eventId.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) {
                        android.widget.Toast.makeText(this, "Event not found", android.widget.Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("EventOrganizerEntrantView", "Failed to load event", e);
                    android.widget.Toast.makeText(this, "Failed to load event. See Logcat.", android.widget.Toast.LENGTH_LONG).show();
                    finish();
                });

        backButton = findViewById(R.id.back_button);
        seeInfo = findViewById(R.id.info_button);
        seeWaitlist = findViewById(R.id.waitlist_see_entrants_button);
        seeInvited = findViewById(R.id.invited_see_entrants_button);
        seeEnrolled = findViewById(R.id.enrolled_see_entrants_button);
        seeCancelled = findViewById(R.id.cancelled_see_entrants_button);

        notifyWaitlist = findViewById(R.id.waitlist_send_notification_button);
        notifyInvited = findViewById(R.id.invited_send_notification_button);
        notifyEnrolled = findViewById(R.id.enrolled_send_notification_button);
        notifyCancelled = findViewById(R.id.cancelled_send_notification_button);
        backButton = findViewById(R.id.back_button);

        sendInvite = findViewById(R.id.waitlist_send_invitation_button);
        updateEvent = findViewById(R.id.info_button);


        //Event event = addMockEvent();


        updateEvent.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, EventModifyView.class);
            myIntent.putExtra("event ID", eventId);
            startActivity(myIntent);
        });

        backButton.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });

        seeInfo.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, EventOrganizerInfoView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        seeWaitlist.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "waitlist");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        seeInvited.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "invited");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        seeEnrolled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "enrolled");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        seeCancelled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "cancelled");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        notifyWaitlist.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, SendNotification.class);
            myIntent.putExtra("List type", "waitList");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        notifyInvited.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, SendNotification.class);
            myIntent.putExtra("List type", "invitedList");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        notifyEnrolled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, SendNotification.class);
            myIntent.putExtra("List type", "enrolledList");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        notifyCancelled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, SendNotification.class);
            myIntent.putExtra("List type", "cancelledList");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });



        /** For inviting entrants and redrawing
        // I believe for redrawing, we need to change the button depending on whether entrants have been redrawn or not
        // I've created the redraw boolean for that case.
        // The button for redrawing could be enabled depending on the following:
           The size of invitelist + acceptedlist DOES NOT equal the invitelistlimit
           In this case, that would mean some people have cancelled and the invitelistlimit is not reached.
           I've already set up doLottery for this case. - Michelle
        **/

        Dialog dialog = new Dialog(this);

        // click on invite button
        sendInvite.setOnClickListener(new View.OnClickListener() {
            // open up fragment to send invite
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.select_batch_from_waitlist);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                Button cancelInvite = dialog.findViewById(R.id.cancel_button);
                Button invite = dialog.findViewById(R.id.invite_button);
                EditText amount = dialog.findViewById(R.id.editTextNumber);

                // click on cancel button to leave fragment
                cancelInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });

                // invite entrants as long as there is an int
                invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(amount.getText().toString())) {
                            amount.setError("Number required");
                            amount.requestFocus();
                            return;
                        }
                        // get invitelistlimit, set and use
                        int value = Integer.parseInt(amount.getText().toString());

                        if ( value > event.getWaitlist().size()){
                            amount.setError("Not enough entrants to invite");
                            amount.requestFocus();
                            return;
                        }

                        event.setInvitelistlimit(value);
                        lottery.doLottery(event);           // refer to Lottery.java in model folder

                        dialog.dismiss();
                    }
                });
                redraw = true;
                dialog.show();

            }

        });

    }


 }

