package com.example.willowevents.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.willowevents.CreateEventUseCase;
import com.example.willowevents.R;
import com.example.willowevents.ValidateData;
import com.example.willowevents.model.Event;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * This View allows for an Organizer that has logged in to edit details of an Event Object
 * that has already been created
 */
public class EventModifyView extends AppCompatActivity {

    private EditText nameEt, descEt, waitlistLimitEt;
    private TextView eventDateTv, regOpenTv, regCloseTv;
    private Button eventDateBtn, regOpenBtn, regCloseBtn, finishEditBtn, cancelBtn;
    private CheckBox limitWaitlistCb;
    private String eventId;
    private final SimpleDateFormat displayFmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);


    //@Nullable private Date eventDate;
    //@Nullable private Date regOpenDate;
   // @Nullable private Date regCloseDate;

    // Use Case handles the actual event Creation
    private CreateEventUseCase useCase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit_view);
        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        Intent origIntent = new Intent(this, EventOrganizerInfoView.class);
        //check for any data sent along side activity change
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            eventId = extras.getString("Event ID");
            if (eventId == null) {
                android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
                startActivity(origIntent);
            }
        } else {
            android.widget.Toast.makeText(this, "Missing event ID", android.widget.Toast.LENGTH_LONG).show();
            startActivity(origIntent);
        }


        //bindViews();

        cancelBtn.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventModifyView.this, EventOrganizerInfoView.class);
            startActivity(myIntent);
        });




    }

    /**
    private void bindViews() {
        nameEt = findViewById(R.id.event_name_entry);
        descEt = findViewById(R.id.additional_details_entry);
        waitlistLimitEt = findViewById(R.id.waitlist_size_limit_entry);

        eventDateTv = findViewById(R.id.event_start_date);
        regOpenTv = findViewById(R.id.registration_open_date);
        regCloseTv = findViewById(R.id.registration_deadline_date);

        eventDateBtn = findViewById(R.id.event_start_date_button);
        regOpenBtn = findViewById(R.id.registration_open_button);
        regCloseBtn = findViewById(R.id.registration_deadline_button);

        cancelBtn = findViewById(R.id.create_event_cancel_button);
        finishEditBtn = findViewById(R.id.create_event_create_button);

        limitWaitlistCb = findViewById(R.id.limit_waitlist_checkbox);
    }

     */

}










