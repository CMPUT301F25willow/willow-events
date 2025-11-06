package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.Lottery;
import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;

import java.util.ArrayList;
import java.util.Random;
import java.util.Random;

public class EventOrganizerEntrantView extends AppCompatActivity {

    private Button seeWaitlist;
    private Button seeInvited;
    private Button seeEnrolled;
    private Button seeCancelled;
    private Button backButton;
    private Button sendInvite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_entrants_view);

        seeWaitlist = findViewById(R.id.waitlist_see_entrants_button);
        seeInvited = findViewById(R.id.invited_see_entrants_button);
        seeEnrolled = findViewById(R.id.enrolled_see_entrants_button);
        seeCancelled = findViewById(R.id.cancelled_see_entrants_button);
        backButton = findViewById(R.id.back_button);

        sendInvite = findViewById(R.id.waitlist_send_invitation_button);

        Event event = addMockEvent();




        backButton.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, MainOrganizerView.class);
            myIntent.putExtra("Type", "mainOrganizerPage");
            startActivity(myIntent);
        });

        seeWaitlist.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "waitlist");
            startActivity(myIntent);
        });

        seeInvited.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "invited");
            startActivity(myIntent);
        });

        seeEnrolled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "enrolled");
            startActivity(myIntent);
        });

        seeCancelled.setOnClickListener(view -> {
            //Switch views
            Intent myIntent = new Intent(EventOrganizerEntrantView.this, UserListView.class);
            myIntent.putExtra("Type", "cancelled");
            startActivity(myIntent);
        });



        // For inviting entrants

        Dialog dialog = new Dialog(this);

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

                cancelInvite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();

                    }
                });

                invite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (TextUtils.isEmpty(amount.getText().toString())) {
                            amount.setError("Number required");
                            amount.requestFocus();
                            return;
                        }

                        int value = Integer.parseInt(amount.getText().toString());


                        ArrayList<Entrant> selected = doLottery(event.getWaitlist(), value);
                        event.setInvitelist(selected);

                        dialog.dismiss();
                    }
                });


                dialog.show();

            }

        });


    }

    public ArrayList<Entrant> doLottery(ArrayList<Entrant> waitlist, int max){

        ArrayList<Entrant> selected = new ArrayList<Entrant>();

        while ( max > 0){

            max-=1;
            int size = waitlist.size();
            int random = new Random().nextInt(size);
            selected.add(waitlist.get(random));
            waitlist.remove(random);

        }


        return selected;
    }



    public Event addMockEvent(){

        Event event = new Event("EventOne");
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Entrant> entrantlist = new ArrayList<>();

        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));

        event.setWaitlist(entrantlist);
        return event;
    }

}