package com.example.willowevents;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;


import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
/*
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
*/

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class EventCreationView extends AppCompatActivity {
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

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    pickedImageUri = uri;
                    uploadedImage.setImageURI(uri);
                    uploadBannerToStorage(uri);
                }
            });



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
            if (limiting) {
                //I will have to implement this somewhere else
                //Need to fix this
                sizeLimit.setFocusable(true);
                sizeLimit.setFocusableInTouchMode(true);
                waitListLimit = sizeLimit.getText().toString();
                //Make sure user input is valid
                if (!ValidateData.containsOnlyDigits(waitListLimit)) {
                    //don't let them
                    sizeLimit.setText("");
                }
            } else {
                sizeLimit.setFocusable(false);
                sizeLimit.setFocusableInTouchMode(false);
            }
        });

        // Firebase
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        //storage = FirebaseStorage.getInstance();


        bindViews();
        hookUpInteractions();
        seedDefaultTimestamps(); // show something sensible before user picks
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

       /* btnCancel.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> createEvent());*/


    }
    private void hookUpInteractions() {
        eventStartBtn.setOnClickListener(v -> pickDateTime(eventStartCal,  eventStartTv));
        regDeadlineBtn.setOnClickListener(v -> pickDateTime(regDeadlineCal,  regDeadlineTv));
        regOpenBtn.setOnClickListener(v -> pickDateTime(regOpenCal, regOpenTv));

        uploadImageBtn.setOnClickListener(v -> imagePicker.launch("image/*"));

        limitWaitlistCb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            waitlistSizeEt.setEnabled(isChecked);
            if (!isChecked) waitlistSizeEt.setText("");
        });

        cancelBtn.setOnClickListener(v -> finish());
        createBtn.setOnClickListener(v -> createEvent());
    }

    private void seedDefaultTimestamps() {
        Date now = new Date();
        eventStartCal.setTime(now);
        regOpenCal.setTime(now);

        regDeadlineCal.setTime(now);
        regDeadlineCal.add(Calendar.HOUR_OF_DAY, 2);

        eventStartTv.setText(displayFmt.format(eventStartCal.getTime()));
        regOpenTv.setText(displayFmt.format(regOpenCal.getTime()));
        regDeadlineTv.setText(displayFmt.format(regDeadlineCal.getTime()));
    }

    private void pickDateTime(Calendar cal, TextView target) {
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

    private void uploadBannerToStorage(@Nullable Uri uri) {//DOESNT WORK, REALIZED THAT FIREBASE STORAGE IS SUBSCRIPTION BASED
        /*if (uri == null) {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference rootRef = storage.getReference(); // root of your default bucket
        String fileName = UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = rootRef.child("event_banners").child(fileName);

        // (Optional) set content type based on selected Uri
       *//* String mime = getContentResolver().getType(uri);
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(mime != null ? mime : "image/jpeg")
                .build();*//*

        // Start upload, then fetch the download URL for the EXACT same ref
        ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    bannerImageUrl = downloadUri.toString();
                    Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });*/

    }
    private void createEvent() {
        String title   = textOf(nameEt);
        String details = textOf(extraDetailsEt);
        // location is optional for now;
        String location = textOf(locationEt);

        //validation
        if (TextUtils.isEmpty(title)) {
            nameEt.setError("Title required");
            nameEt.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(details)) {
            extraDetailsEt.setError("Details required");
            extraDetailsEt.requestFocus();
            return;
        }

        //validate times from the calendars
        Date startsAt = eventStartCal.getTime();
        Date regOpen  = regOpenCal.getTime();
        Date regEnd   = regDeadlineCal.getTime();

        if (!regEnd.after(regOpen)) {
            Toast.makeText(this, "Registration deadline must be after open date", Toast.LENGTH_LONG).show();
            return;
        }
        if (!regEnd.before(startsAt)) {
            Toast.makeText(this, "Registration deadline must be before event start", Toast.LENGTH_LONG).show();
            return;
        }
//validate waitListLimit
        Integer waitlistLimit = null;
        if (limitWaitlistCb.isChecked()) {
            String n = textOf(waitlistSizeEt);
            if (n.matches("\\d+")) {
                waitlistLimit = Integer.parseInt(n);
            } else {
                waitlistSizeEt.setError("Digits only");
                waitlistSizeEt.requestFocus();
                return;
            }
        }


       //Build Event
        Event e = new Event(
                title,
                details,
                "This is lottery details which is seen in a scrollable form",
                null,                              // firebase id
                bannerImageUrl,                    //null for now
                regOpen,
                regEnd,
                startsAt
        );



        // e.setLocation(location); IMPLEMENT THIS LATER
        e.setWaitlistLimit(waitlistLimit);

        // Initialize lists to avoid nulls (adjust generics based on your Event.java)
        e.setWaitlist(new ArrayList<>());
        e.setAprovelist(new ArrayList<>());
        e.setCancellist(new ArrayList<>());




        // Write to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference doc = db.collection("events").document();
        e.setId(doc.getId());


        doc.set(e)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Event created!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(err ->
                        Toast.makeText(this, "Failed: " + err.getMessage(), Toast.LENGTH_LONG).show());
    }

    private static String textOf(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    // Helper if you ever need to parse from TextView text back to Date
    private @Nullable Date parseDisplayTime(TextView tv) {
        try {
            return displayFmt.parse(tv.getText().toString().trim());
        } catch (ParseException e) {
            return null;
        }

    }}
