package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.willowevents.R;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.entrant.EventEntrantView;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * This View shows event information from the organizer's perspective,
 * with options to update information or change settings.
 */
public class EventOrganizerInfoView extends AppCompatActivity {
    private String eventId;
    private Button backButton, seeEntrants, uploadImage, editEvent, qrButton;
    private TextView time, details, eventName;
    private ImageView posterImage, newImage;
    private Event event;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    // URI of the image from users device
    private Uri imageUri;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_organizer_info_view);
        db = FirebaseFirestore.getInstance();


        TextView userNameText = findViewById(R.id.username);
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

        //check for any data sent along side activity change
        Intent origIntent = new Intent(this, EventOrganizerEntrantView.class);
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

        if (eventId == null) {
            return;
        }


        bindViews();


        // Load in event data
        // Imma be honest IDK how the firebase works I just plugged stuff in and it worked
        db.collection("events").document(eventId).get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                return;
            }
            event = snapshot.toObject(Event.class);

            String title = event.getTitle() + "\n"
                    + event.getEventDate();
            time.setText(title);

            if (event == null) {
                return;
            }

            // DISPLAY POSTER IMAGE IF EXISTS, COLLAPSE IF DOESNT
            String posterUrl = event.getPosterUrl();
            if (posterUrl != null && !posterUrl.isEmpty()) {
                Glide.with(this)
                        .load(posterUrl)
                        .into(posterImage);
            } else {
                posterImage.setVisibility(View.GONE);
            }

            details.setText(event.getDescription());




        });


        db = FirebaseFirestore.getInstance();

        db.collection("events")
                .document(eventId)
                .get()
                .addOnSuccessListener(doc -> {
                    event = doc.toObject(Event.class);

                    if (event == null) {
                        android.widget.Toast.makeText(
                                EventOrganizerInfoView.this,
                                "Could not find event data",
                                android.widget.Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    // Fill in UI with event data
//                    if (eventName != null) {
//                        eventName.setText(event.getTitle());
//                    }

                    //set up QR button
                    qrButton.setOnClickListener(v -> {
                        Intent intent = new Intent(EventOrganizerInfoView.this, QrCodeActivity.class);
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_NAME, event.getTitle());
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_DESCRIPTION, event.getDescription());
                        intent.putExtra(QrCodeActivity.EXTRA_EVENT_POSTER_URL, event.getPosterUrl());  // 👈 NEW
                        startActivity(intent);
                    });
                })
                .addOnFailureListener(e -> {
                    android.widget.Toast.makeText(
                            EventOrganizerInfoView.this,
                            "Failed to load event: " + e.getMessage(),
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                });




        //TODO: event = get event from database with eventId


        seeEntrants.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventOrganizerEntrantView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });

        TextView eventName = findViewById(R.id.username);
        //Other initializations

//        eventName.setText(event.getTitle());
        //Other sets

        backButton.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, MainOrganizerView.class);
            startActivity(myIntent);
        });

        // will be unused/uneeded
        editEvent.setOnClickListener(view -> {
            Intent myIntent = new Intent(EventOrganizerInfoView.this, EventModifyView.class);
            myIntent.putExtra("Event ID", eventId);
            startActivity(myIntent);
        });


        // updates image
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    //  Check if we got data
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        //  Get URI of the selected image and show in the imageview
                        imageUri = result.getData().getData();
                        newImage.setImageURI(imageUri); // show selected image
                    }
                }
        );


        // For updating Poster for Event
        Dialog dialog = new Dialog(this);

        // Uploads a new image to the event
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
                newImage = dialog.findViewById(R.id.uploaded_image);


                // click on cancel button to leave fragment
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                // HOW DO YOU DELETE THE OLD IMAGE????
                // replace old poster with new one, refer to uploadButton
                // Replace old poster with the newly selected image
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Make sure the user actually picked an image
                        if (imageUri == null) {
                            Toast.makeText(EventOrganizerInfoView.this,
                                    "Please select an image first", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Make sure we have a valid event ID
                        if (TextUtils.isEmpty(eventId)) {
                            Toast.makeText(EventOrganizerInfoView.this,
                                    "Missing event ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Decide on a storage file name for this event's poster
                        //  All posters go in event_posters in Firebase Storage
                        String fileName = "poster_" + eventId + ".jpg";

                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference posterRef = storage
                                .getReference()
                                .child("event_posters")
                                .child(fileName);

                        // Upload the selected image to Firebase Storage
                        posterRef.putFile(imageUri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Once upload succeeds, get the download URL
                                    posterRef.getDownloadUrl()
                                            .addOnSuccessListener(downloadUri -> {
                                                String url = downloadUri.toString();

                                                // Update the Event object locally
                                                if (event != null) {
                                                    event.setPosterUrl(url);
                                                }

                                                // Show new image on the main screen immediately
                                                if (posterImage != null) {
                                                    Glide.with(EventOrganizerInfoView.this)
                                                            .load(url)
                                                            .into(posterImage);
                                                }

                                                // Save the URL into Firestore
                                                db.collection("events")
                                                        .document(eventId)
                                                        .update("posterUrl", url)
                                                        .addOnSuccessListener(unused -> {
                                                            Toast.makeText(
                                                                    EventOrganizerInfoView.this,
                                                                    "Poster updated",
                                                                    Toast.LENGTH_SHORT
                                                            ).show();
                                                            dialog.dismiss();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(
                                                                    EventOrganizerInfoView.this,
                                                                    "Poster uploaded but failed to save: " + e.getMessage(),
                                                                    Toast.LENGTH_SHORT
                                                            ).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(
                                                        EventOrganizerInfoView.this,
                                                        "Failed to get download URL: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(
                                            EventOrganizerInfoView.this,
                                            "Failed to upload image: " + e.getMessage(),
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                    }
                });

                uploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Picture"));
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
        qrButton = findViewById(R.id.qr_code_button);
        eventName = findViewById(R.id.username);
        details = findViewById(R.id.event_details);

        details.setMovementMethod(new ScrollingMovementMethod());

        // Not in use
        editEvent = findViewById(R.id.edit_button);
        editEvent.setVisibility(View.GONE);

    }




}