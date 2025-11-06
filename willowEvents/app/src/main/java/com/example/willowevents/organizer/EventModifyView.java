package com.example.willowevents.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
 * This class is where the organizer modifies the event info.
 * It is largely the same as EventCreationView, but all the
 * options should be auto filled with the current event info.
 */
public class EventModifyView extends AppCompatActivity {
    /***
    /**
     * Effectively does the same thing as EventCreationView..
     */
    private EditText nameEt;                  // @id/event_name_entry
    private EditText locationEt;              // @id/event_location_entry (kept; optional)
    private TextView eventStartTv;            // @id/event_start_date
    private TextView regDeadlineTv;           // @id/registration_deadline_date
    private TextView regOpenTv;               // @id/registration_open_date
    private Button eventStartBtn;             // @id/event_start_date_button
    private Button regDeadlineBtn;            // @id/registration_deadline_button
    private Button regOpenBtn;                // @id/registration_open_button
    private Button uploadImageBtn;            // @id/upload_image_button
    private ImageView uploadedImage;          // @id/uploaded_image
    private EditText extraDetailsEt;          // @id/additional_details_entry
    private CheckBox limitWaitlistCb;         // @id/limit_waitlist_checkbox
    private EditText waitlistSizeEt;          // @id/waitlist_size_limit_entry
    private Button cancelBtn;                 // @id/create_event_cancel_button
    private Button createBtn;

   /* private EditText etTitle;
    private EditText etBannerUrl;
    private EditText etStart;
    private EditText etEnd;
    private EditText etDetails;*/

    private final Calendar eventStartCal = Calendar.getInstance();
    private final Calendar regDeadlineCal = Calendar.getInstance();
    private final Calendar regOpenCal = Calendar.getInstance();
    private Uri pickedImageUri = null; //uri = uniform resource identifier
    private String bannerImageUrl = null; // set after upload

    private FirebaseFirestore db;
    //private FirebaseStorage storage; //for banner images EDIT: FIREBASE STORAGE ISNT FREE FIND ALTERNATIVE
    private final DateFormat displayFmt = new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault());

    private EditText sizeLimit;
    private CheckBox doLimit;
    private boolean limiting = false;
    private String waitListLimit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit_view);

        // Intent stuff
        bindViews();


    }

    private void bindViews() {
        // Bind views
        nameEt         = findViewById(R.id.event_name_entry);
        locationEt     = findViewById(R.id.event_location_entry);
        eventStartTv   = findViewById(R.id.event_start_date);
        regDeadlineTv  = findViewById(R.id.registration_deadline_date);
        regOpenTv      = findViewById(R.id.registration_open_date);

        eventStartBtn  = findViewById(R.id.event_start_date_button);
        regDeadlineBtn = findViewById(R.id.registration_deadline_button);
        regOpenBtn     = findViewById(R.id.registration_open_button);

        uploadImageBtn = findViewById(R.id.upload_image_button);
        uploadedImage  = findViewById(R.id.uploaded_image);

        extraDetailsEt = findViewById(R.id.additional_details_entry);

        limitWaitlistCb = findViewById(R.id.limit_waitlist_checkbox);
        waitlistSizeEt  = findViewById(R.id.waitlist_size_limit_entry);

        cancelBtn      = findViewById(R.id.create_event_cancel_button);
        createBtn      = findViewById(R.id.create_event_create_button);

    }




}










