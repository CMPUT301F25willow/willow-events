package com.example.willowevents.organizer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.willowevents.FirebaseMediaRepository;
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
import com.google.firebase.storage.FirebaseStorage;
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
/**
 * Activity that allows an organizer to create a new event.
 * <p>
 * This screen collects all user input required to define an event:
 * title, description, event dates, waitlist configuration, geolocation requirement,
 * and an optional poster image. Once the user taps "Create", it builds a
 * {@link CreateEventUseCase.Request} and delegates the actual creation work
 * to {@link CreateEventUseCase}.
 */

/**
 * This view is what an organizer sees when they create an event
 * and stores relevant info about the event in an Event to be used
 * elsewhere
 */
public class EventCreationView extends AppCompatActivity {
    //Initialize Fields
    private EditText nameEt, descEt, waitlistLimitEt;
    private TextView eventDateTv, regOpenTv, regCloseTv;
    private Button eventDateBtn, regOpenBtn, regCloseBtn, createBtn, cancelBtn, uploadImageBtn;
    private CheckBox limitWaitlistCb, geolocCb;

    private ImageView uploaded_image;


    private final SimpleDateFormat displayFmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);


    @Nullable private Date eventDate;
    @Nullable private Date regOpenDate;
    @Nullable private Date regCloseDate;

    // Use Case handles the actual event Creation
    private CreateEventUseCase useCase;
    // Launcher for the image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    // URI of the image from users device
    private Uri imageUri;


    /**
     * Standard Android lifecycle callback invoked when the Activity is first created.
     * <p>
     * This method:
     * <ul>
     *     <li>Inflates the layout for event creation</li>
     *     <li>Binds Java fields to XML views</li>
     *     <li>Instantiates repositories and the {@link CreateEventUseCase}</li>
     *     <li>Registers click listeners for buttons</li>
     *     <li>Registers an {@link ActivityResultLauncher} for picking an image</li>
     * </ul>
     *
     * @param savedInstanceState previous state if the Activity is being re-created,
     *                           or {@code null} for a fresh launch
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);

        // Connect Java fields to XML views
        bindViews();
        //  Set up repositories
        wireDependencies();
        // Attack click listeners to buttons
        wireClicks();
        //When the user finishes picking an image, this code will run
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //  Check if we got data
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //  Get URI of the selected image and show in the imageview
                        imageUri = result.getData().getData();
                        uploaded_image.setImageURI(imageUri); // show selected image
                    }
                }
        );
    }
    /**
     * Binds member fields to their corresponding views in the layout.
     * <p>
     * This method should be called after {@link #setContentView(int)} so that
     * {@link #findViewById(int)} can locate the views defined in XML.
     */
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
        uploadImageBtn = findViewById(R.id.upload_image_button);


        limitWaitlistCb = findViewById(R.id.limit_waitlist_checkbox);
        geolocCb = findViewById(R.id.geolocation_checkbox);
        uploaded_image = findViewById(R.id.uploaded_image);
    }
    /**
     * Creates and wires up the data-layer dependencies used by this screen.
     * <p>
     * In particular, this method:
     * <ul>
     *     <li>Instantiates a Firestore-backed {@link EventRepository}</li>
     *     <li>Instantiates a Firebase Storage-backed {@link MediaRepository}</li>
     *     <li>Creates the {@link CreateEventUseCaseImpl} that coordinates event creation</li>
     * </ul>
     */
    private void wireDependencies() {
        EventRepository events = new FirestoreEventRepository(FirebaseFirestore.getInstance());
        MediaRepository media = new FirebaseMediaRepository(FirebaseStorage.getInstance());
        // usecase combines the two to implement events with no poster
        useCase = new CreateEventUseCaseImpl(events, media);
    }

    /**
     * Attaches click listeners to all interactive UI elements.
     * <p>
     * This method wires:
     * <ul>
     *     <li>Date/time pickers for event start, registration open, and registration close</li>
     *     <li>The "Create" button to {@link #onCreateClicked()}</li>
     *     <li>The "Cancel" button to simply finish the Activity</li>
     *     <li>The "Upload Image" button to launch an image picker</li>
     * </ul>
     */
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

        // Create event when Create button is clicked
        createBtn.setOnClickListener(v -> onCreateClicked());
        // Close and go back when cancel button is clicked
        cancelBtn.setOnClickListener(v -> finish());
        // Let the user pick an image from device when they click upload
        uploadImageBtn.setOnClickListener((v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
        }));
    }
    /**
     * Functional interface used to pass a callback that consumes a {@link Date}
     * value produced by {@link #pickDateTime(DateConsumer)}.
     */
    private interface DateConsumer { void accept(Date d); }

    /**
     * Opens a {@link DatePickerDialog} followed by a {@link TimePickerDialog},
     * then composes the chosen date and time into a single {@link Date} and passes it
     * to the provided {@link DateConsumer} callback.
     *
     * @param consumer callback that receives the combined date and time selected by the user
     */
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
    // Called when User presses the Create button
    private void onCreateClicked() {
        // Read inputs
        String title = textOf(nameEt);
        String description = textOf(descEt);
        // Check if title or Description is empty
        if (title.isEmpty()) {
            nameEt.setError("Title required");
            return;
        }
        if (description.isEmpty()) {
            descEt.setError("Description required");
            return;
        }
        // Use the Device's android ID as the organizer Id
        String organizerId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        // Parse and validate the optional waitlist limit
        Integer waitlistLimit = null;
        if (limitWaitlistCb.isChecked()) {
            String n = textOf(waitlistLimitEt);
            if (TextUtils.isEmpty(n) || !n.matches("\\d+")) {
                waitlistLimitEt.setError("Digits only");
                return;
            }
            waitlistLimit = Integer.parseInt(n);
        }

        // Build request object for the CreateEventUseCase
        CreateEventUseCase.Request r = new CreateEventUseCase.Request();
        r.title = title;
        r.description = description;
        r.eventDate = eventDate != null ? eventDate : parseDisplayTime(eventDateTv);
        r.registrationOpenDate = regOpenDate != null ? regOpenDate : parseDisplayTime(regOpenTv);
        r.registrationCloseDate = regCloseDate != null ? regCloseDate : parseDisplayTime(regCloseTv);
        r.capacity = null; // not in this UI now
        r.waitlistLimit = waitlistLimit; // optional
        r.organizerId = organizerId;
        r.posterUri = imageUri;
        r.requireGeo = geolocCb.isChecked();


// Execute the use case
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) {
                // If everything worked, display a success message and close screen
                Toast.makeText(EventCreationView.this, "Event created", Toast.LENGTH_SHORT).show();
                finish(); // return to previous screen
            }
            @Override public void onError(Throwable t) {
                //Display error message on failure
                Toast.makeText(EventCreationView.this, String.valueOf(t.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    //Helper to safely get trimmed text from an EditText. Never returns null
    private String textOf(EditText et) { return et.getText() == null ? "" : et.getText().toString().trim(); }

    //Try to parse a date from the TextView using the same display format, if it fails return null instead of crashing
    private @Nullable Date parseDisplayTime(TextView tv) {
        try {
            return displayFmt.parse(tv.getText().toString().trim());
        } catch (ParseException e) {
            return null;
        }
    }
}

