package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.willowevents.R;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.entrant.EventEntrantView;
import com.example.willowevents.model.Event;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This View shows event information from the organizer's perspective,
 * with options to update information or change settings.
 */
public class EventOrganizerInfoView extends AppCompatActivity {
    private String eventId;
    private Button backButton, seeEntrants, uploadImage, editEvent;
    private TextView time, details, heading;
    private ImageView posterImage;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_info_view);
        FirebaseFirestore db = FirebaseFirestore.getInstance();




        //check for any data sent along side activity change

        Bundle extras = getIntent().getExtras();
        eventId = extras.getString("Event ID");
        if (eventId == null || eventId.trim().isEmpty()) {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bindViews();


        db.collection("events").document(eventId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()){
                return;
            }
            currentEvent = snapshot.toObject(Event.class);

            String title = currentEvent.getTitle() + "\n"
                    + currentEvent.getEventDate();
            time.setText(title);

            if (currentEvent == null){
                return;
            }

            // DISPLAY POSTER IMAGE IF EXISTS, COLLAPSE IF DOESNT
            String posterUrl = currentEvent.getPosterUrl();
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(this)
                        .load(posterUrl)
                        .into(posterImage);
            } else {
                posterImage.setVisibility(View.GONE);
            }

            details.setText(currentEvent.getDescription());

            /**
            //update the event back into firestore
            db.collection("events")
                    .document(eventId)
                    .update(
                            "waitlist", event.getWaitlist(),
                            "inviteList", event.getInviteList(),
                            "approvedList", event.getApprovedList(),
                            "invitelistlimit", event.getInvitelistlimit()
                    );
                **/
        });






        //TODO: event = get event from database with eventId


        seeEntrants.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        //TextView eventName = findViewById(R.id.eventName);
        //Other initializations

//        eventName.setText(event.getTitle());
        //Other sets

        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });


        editEvent.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventModifyView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });




        // For updating Poster for Event
        Dialog dialog = new Dialog(this);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            // open up fragment to update Poster
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.update_poster);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                Button cancelButton = dialog.findViewById(R.id.cancel_button);
                Button uploadButton = dialog.findViewById(R.id.upload_image_button);
                Button updateButton = dialog.findViewById(R.id.update_button);
                ImageView newPoster = dialog.findViewById(R.id.uploaded_image);

                // click on cancel button to leave fragment
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // remove all uploaded changes

                        dialog.dismiss();
                    }
                });

                // replace old poster with new one, refer to uploadButton
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // poster <- new poster
                        dialog.dismiss();
                    }
                });

                // Upload a poster image into firebase idk how that works srry
                // poster has to be temporarily uploaded somewhere
                // when update button is clicked, then replace current poster with new one
                // otherwise discard the new poster and keep the old one
                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // temp poster <- new poster
                        // set newPoster to the new image
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

        });

    }

    /**
     * Binds member fields to their corresponding views in the layout.
     * <p>
     * This method should be called after {@link #setContentView(int)} so that
     * {@link #findViewById(int)} can locate the views defined in XML.
     */
    private void bindViews() {

        time = findViewById(R.id.event_info);
        backButton = findViewById(R.id.back_button);
        seeEntrants = findViewById(R.id.entrants_button);
        uploadImage = findViewById(R.id.upload_image_button);
        posterImage  = findViewById(R.id.uploaded_image);
        details = findViewById(R.id.event_details);

        details.setMovementMethod(new ScrollingMovementMethod());

        // Not in use
        editEvent = findViewById(R.id.edit_button);
        editEvent.setVisibility(View.GONE);

    }




}