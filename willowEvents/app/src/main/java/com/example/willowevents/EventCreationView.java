package com.example.willowevents;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CheckBox;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventCreationView extends AppCompatActivity {

    private EditText etTitle;
    private EditText etBannerUrl;
    private EditText etStart;
    private EditText etEnd;
    private EditText etDetails;

    private final Calendar startCal = Calendar.getInstance();
    private final Calendar endCal = Calendar.getInstance();

    private FirebaseFirestore db;
    private final DateFormat displayFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());



    private EditText sizeLimit;
    private CheckBox doLimit;
    private boolean limiting = false;
    private String waitListLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sizeLimit = findViewById(R.id.waitlist_size_limit_entry);
        doLimit = findViewById(R.id.limit_waitlist_checkbox);
        doLimit.setOnClickListener(view -> {
            limiting = true;
        });
        sizeLimit.setOnClickListener(view -> {
            if(limiting){
                //I will have to implement this somewhere else
                //Need to fix this
                sizeLimit.setFocusable(true);
                sizeLimit.setFocusableInTouchMode(true);
                waitListLimit = sizeLimit.getText().toString();
                //Make sure user input is valid
                if(!ValidateData.containsOnlyDigits(waitListLimit)){
                    //don't let them
                    sizeLimit.setText("");
                }
            }
            else {
                sizeLimit.setFocusable(false);
                sizeLimit.setFocusableInTouchMode(false);
            }
        });

        // Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // Bind views
        etTitle     = findViewById(R.id.enter_name);
        etBannerUrl = findViewById(R.id.enter_banner_url);
        etStart     = findViewById(R.id.enter_start_time);
        etEnd       = findViewById(R.id.enter_end_time);
        etDetails   = findViewById(R.id.enter_event_details);

        Button btnCancel = findViewById(R.id.button);     // existing Cancel
        Button btnCreate = findViewById(R.id.button2);    // existing Create

        // Date/time pickers
        etStart.setOnClickListener(v -> pickDateTime(startCal, etStart));
        etEnd.setOnClickListener(v -> pickDateTime(endCal, etEnd));

        btnCancel.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createEvent());


    }

    private void pickDateTime(Calendar cal, EditText target) {
        //pick date
        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    //pick time
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int minute = cal.get(Calendar.MINUTE);
                    TimePickerDialog tp = new TimePickerDialog(
                            this,
                            (timeView, h, m) -> {
                                cal.set(Calendar.HOUR_OF_DAY, h);
                                cal.set(Calendar.MINUTE, m);
                                cal.set(Calendar.SECOND, 0);
                                cal.set(Calendar.MILLISECOND, 0);
                                target.setText(displayFmt.format(cal.getTime()));
                            },
                            hour, minute, true
                    );
                    tp.show();
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void createEvent() {
        String title   = safeText(etTitle);
        String details = safeText(etDetails);
        String banner  = safeText(etBannerUrl);

        //validation
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Title required");
            etTitle.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(details)) {
            etDetails.setError("Details required");
            etDetails.requestFocus();
            return;
        }
        // Start must be before end
        Date startsAt = startCal.getTime();
        Date endsAt   = endCal.getTime();
        if (etStart.getText().length() == 0 || etEnd.getText().length() == 0) {
            Toast.makeText(this, "Please pick start and end times", Toast.LENGTH_LONG).show();
            return;
        }
        if (!endsAt.after(startsAt)) {
            Toast.makeText(this, "End time must be after start time", Toast.LENGTH_LONG).show();
            return;
        }

        // Build Event
        Event e = new Event(
                title,
                details,
                "This is lottery details which is seen in a scrollable form",
                null,        // id
                TextUtils.isEmpty(banner) ? null : banner,
                startsAt,
                endsAt
        );



        e.setWaitlist(new ArrayList<>());
        e.setAprovelist(new ArrayList<>());
        e.setCancellist(new ArrayList<>());




        // Write to Firestore
        DocumentReference doc = db.collection("events").document();
        e.setId(doc.getId());


        doc.set(e)
                .addOnSuccessListener(_v -> {
                    Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                    finish();  // return to previous screen;
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Failed: " + err.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private static String safeText(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    }
