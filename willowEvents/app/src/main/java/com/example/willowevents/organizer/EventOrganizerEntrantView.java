package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Notification;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Lottery;
import com.example.willowevents.model.User;
import com.google.android.gms.maps.MapView;
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
    private TextView userNameText;
    private boolean redraw = false;


    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_entrants_view);

        // --- View bindings ---
        backButton   = findViewById(R.id.back_button);
        seeInfo      = findViewById(R.id.info_button);
        seeWaitlist  = findViewById(R.id.waitlist_see_entrants_button);
        seeInvited   = findViewById(R.id.invited_see_entrants_button);
        seeEnrolled  = findViewById(R.id.enrolled_see_entrants_button);
        seeCancelled = findViewById(R.id.cancelled_see_entrants_button);
        sendInvite   = findViewById(R.id.waitlist_send_invitation_button);
        notifyWaitlist = findViewById(R.id.waitlist_send_notification_button);
        notifyInvited = findViewById(R.id.invited_send_notification_button);
        notifyEnrolled = findViewById(R.id.enrolled_send_notification_button);
        notifyCancelled = findViewById(R.id.cancelled_send_notification_button);
        userNameText = findViewById(R.id.username);

        // updateEvent  = findViewById(R.id.update_event_button); // <- only if this exists in XML

        sendInvite.setEnabled(false); // we only enable once event is loaded

        // --- Get Event ID from extras ---
        Intent origIntent = new Intent(this, MainOrganizerView.class);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            eventId = extras.getString("Event ID");
        } else {
            // fallback if coming from somewhere unexpected
            origIntent = new Intent(this, EventOrganizerInfoView.class);
            extras = getIntent().getExtras();
            if (extras != null) {
                eventId = extras.getString("Event ID");
            }
        }

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "Missing event ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Load Event from Firestore ---
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    event = snap.toObject(Event.class);
                    if (event == null) {
                        Toast.makeText(this, "Failed to load event data", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    // Now it's safe to enable sendInvite
                    sendInvite.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("EventOrganizerEntrantView", "Failed to load event", e);
                    Toast.makeText(this, "Failed to load event. See Logcat.", Toast.LENGTH_LONG).show();
                    finish();
                });

        String userID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        UserController userController = new UserController();
        userController.getUser(userID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                User currentUser = user;
                // VIEWS AND INTERACTIBLES
                userNameText.setText(currentUser.getName());
            }
        });


        // --- Button: back ---
        backButton.setOnClickListener(v -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });

        // --- Button: event info ---
        seeInfo.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, EventOrganizerInfoView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        // --- Button: see waitlist ---
        seeWaitlist.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "waitlist");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        // --- Button: see invited ---
        seeInvited.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "invited");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        // --- Button: see enrolled ---
        seeEnrolled.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "enrolled");
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        // --- Button: see cancelled ---
        seeCancelled.setOnClickListener(view -> {
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


        /*
         * For inviting entrants and redrawing
         * I believe for redrawing, we need to change the button depending on whether entrants have been redrawn or not
         * I've created the redraw boolean for that case.
         * The button for redrawing could be enabled depending on the following:
         *   The size of invitelist + acceptedlist DOES NOT equal the invitelistlimit
         *   In this case, that would mean some people have cancelled and the invitelistlimit is not reached.
         * I've already set up doLottery for this case. - Michelle
         */

        Dialog dialog = new Dialog(this);

        // --- Button: send invites from waitlist ---
        sendInvite.setOnClickListener(new View.OnClickListener() {
            // open up fragment to send invite
            @Override
            public void onClick(View view) {

                if (event == null) {
                    Toast.makeText(EventOrganizerEntrantView.this,
                            "Event data not loaded yet", Toast.LENGTH_LONG).show();
                    return;
                }

                dialog.setContentView(R.layout.select_batch_from_waitlist);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);

                Button cancelInvite = dialog.findViewById(R.id.cancel_button);
                Button invite = dialog.findViewById(R.id.invite_button);
                EditText amount = dialog.findViewById(R.id.editTextNumber);

                // click on cancel button to leave fragment
                cancelInvite.setOnClickListener(v -> dialog.dismiss());

                // invite entrants as long as there is an int
                invite.setOnClickListener(v -> {

                    if (TextUtils.isEmpty(amount.getText().toString())) {
                        amount.setError("Number required");
                        amount.requestFocus();
                        return;
                    }

                    int value = Integer.parseInt(amount.getText().toString());

                    //do we even have this many on the waitlist?
                    if (event.getWaitlist() == null || value > event.getWaitlist().size()) {
                        amount.setError("Not enough entrants to invite");
                        amount.requestFocus();
                        return;
                    }

                    // set invite list limit and run lottery
                    event.setInvitelistlimit(value);
                    doLottery(event);

                    // save updated lists back to Firestore
                    db.collection("events").document(eventId)
                            .update(
                                    "waitlist", event.getWaitlist(),
                                    "inviteList", event.getInviteList(),
                                    "approvedList", event.getApprovedList(),
                                    "invitelistlimit", event.getInvitelistlimit()
                            )
                            .addOnSuccessListener(unused ->
                                    Toast.makeText(EventOrganizerEntrantView.this,
                                            "Invites sent", Toast.LENGTH_SHORT).show()
                            )
                            .addOnFailureListener(e ->
                                    Toast.makeText(EventOrganizerEntrantView.this,
                                            "Failed to update event", Toast.LENGTH_LONG).show()
                            );

                    dialog.dismiss();
                });

                redraw = true;
                dialog.show();
            }
        });
    }

    /**
     * For a max value, a max number of random entrants will be chosen from an event's
     * WaitList and be moved to the event's InviteList.
     * max refers to the value of inviteListLimit - size of InviteList - size of ApprovedList
     *
     * @param event Event whose lists are to be updated
     */
    public void doLottery(Event event) {

        int max = event.getInvitelistlimit()
                - event.getInviteList().size()
                - event.getApprovedList().size();

        while (max > 0 && event.getWaitlist() != null && !event.getWaitlist().isEmpty()) {
            max -= 1;

            int size = event.getWaitlist().size();
            int random = new Random().nextInt(size);

            // move entrant from waitlist → inviteList
            event.getInviteList().add(event.getWaitlist().get(random));
            event.getWaitlist().remove(random); // remove from waitlist, not inviteList
        }
    }
}