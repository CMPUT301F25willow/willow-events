package com.example.willowevents.organizer;

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


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.example.willowevents.CreateEventUseCase;
import com.example.willowevents.CreateEventUseCaseImpl;
import com.example.willowevents.EventRepository;
import com.example.willowevents.FirestoreEventRepository;
import com.example.willowevents.MediaRepository;
import com.example.willowevents.model.Event;
import com.example.willowevents.R;
import com.example.willowevents.ValidateData;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
/*
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
*/
import androidx.appcompat.app.AppCompatActivity;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class EventCreationView extends AppCompatActivity {
    private EditText nameEt, descEt, waitlistLimitEt;
    private TextView eventDateTv, regOpenTv, regCloseTv;
    private Button eventDateBtn, regOpenBtn, regCloseBtn, createBtn, cancelBtn;
    private CheckBox limitWaitlistCb;


    private final SimpleDateFormat displayFmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);


    @Nullable private Date eventDate;
    @Nullable private Date regOpenDate;
    @Nullable private Date regCloseDate;


    private CreateEventUseCase useCase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);


        bindViews();
        wireDependencies();
        wireClicks();
    }

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


        createBtn = findViewById(R.id.create_event_create_button);
        cancelBtn = findViewById(R.id.create_event_cancel_button);


        limitWaitlistCb = findViewById(R.id.limit_waitlist_checkbox);
    }
    private void wireDependencies() {
        EventRepository events = new FirestoreEventRepository(FirebaseFirestore.getInstance());
        MediaRepository media = (uri, eventId, cb) -> cb.onError(new UnsupportedOperationException("Poster upload not implemented yet"));
        useCase = new CreateEventUseCaseImpl(events, media);
    }


    private void wireClicks() {
        eventDateBtn.setOnClickListener(v -> pickDateTime(d -> {
            eventDate = d; eventDateTv.setText(displayFmt.format(d));
        }));
        regOpenBtn.setOnClickListener(v -> pickDateTime(d -> {
            regOpenDate = d; regOpenTv.setText(displayFmt.format(d));
        }));
        regCloseBtn.setOnClickListener(v -> pickDateTime(d -> {
            regCloseDate = d; regCloseTv.setText(displayFmt.format(d));
        }));


        createBtn.setOnClickListener(v -> onCreateClicked());
        cancelBtn.setOnClickListener(v -> finish());
    }
    private interface DateConsumer { void accept(Date d); }

    private void pickDateTime(DateConsumer consumer) {
        final Calendar cal = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this,
                (view, year, month, day) -> {
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                    TimePickerDialog tpd = new TimePickerDialog(this,
                            (timeView, hour, minute) -> {
                                cal.set(Calendar.HOUR_OF_DAY, hour);
                                cal.set(Calendar.MINUTE, minute);
                                cal.set(Calendar.SECOND, 0);
                                consumer.accept(cal.getTime());
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            true);
                    tpd.show();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }
    private void onCreateClicked() {
        String title = textOf(nameEt);
        String description = textOf(descEt);
        //DEV HACKS ----- THIS IS TO OVERRIDE THE ORGANIZER ID WHEN DEVELOPING
       /* String organizerId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;*/
        String organizerId = null;
        var user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            organizerId = user.getUid();
        } else  {
            organizerId = "dev-uid-123"; // DEV-ONLY fallback
        }


        Integer waitlistLimit = null;
        if (limitWaitlistCb.isChecked()) {
            String n = textOf(waitlistLimitEt);
            if (n.matches("\\d+")) {
                waitlistLimit = Integer.parseInt(n);
            } else if (!TextUtils.isEmpty(n)) {
                waitlistLimitEt.setError("Digits only");
                return;
            }
        }

        // Build request
        CreateEventUseCase.Request r = new CreateEventUseCase.Request();
        r.title = title;
        r.description = description;
        r.eventDate = eventDate != null ? eventDate : parseDisplayTime(eventDateTv);
        r.registrationOpenDate = regOpenDate != null ? regOpenDate : parseDisplayTime(regOpenTv);
        r.registrationCloseDate = regCloseDate != null ? regCloseDate : parseDisplayTime(regCloseTv);
        r.capacity = null; // not in this UI now
        r.waitlistLimit = waitlistLimit; // optional
        r.organizerId = organizerId;
        r.posterUri = null; // reserved
        r.requireGeo = false; // reserved


// Execute
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) {
                Toast.makeText(EventCreationView.this, "Event created", Toast.LENGTH_SHORT).show();
                finish(); // return to previous screen
            }
            @Override public void onError(Throwable t) {
                Toast.makeText(EventCreationView.this, String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String textOf(EditText et) { return et.getText() == null ? "" : et.getText().toString().trim(); }


    private @Nullable Date parseDisplayTime(TextView tv) {
        try {
            return displayFmt.parse(tv.getText().toString().trim());
        } catch (ParseException e) {
            return null;
        }
    }
}

