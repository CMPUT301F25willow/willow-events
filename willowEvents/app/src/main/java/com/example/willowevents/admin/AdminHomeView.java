package com.example.willowevents.admin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.ProfileView;
import com.example.willowevents.R;
import com.example.willowevents.arrayAdapters.InviteArrayAdapter;
import com.example.willowevents.arrayAdapters.NotificationArrayAdapter;
import com.example.willowevents.controller.EventController;
import com.example.willowevents.controller.NotificationController;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Image;
import com.example.willowevents.model.Invite;
import com.example.willowevents.model.Notification;
import com.example.willowevents.model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Admin home screen.
 *
 * This Activity lets an admin:
 *  - Browse all events (from Firestore "events")
 *  - Browse all users (from Firestore "users")
 *  - Browse event poster images (from Firebase Storage "event_posters/")

 */
public class AdminHomeView extends AppCompatActivity {

    // =========================================================================
    // UI FIELDS
    // =========================================================================

    // ListView that shows events, users, images, or notifications,
    // depending on which "mode" we are in.
    private ListView eventView;

    // Top navigation buttons
    private Button browseEventsButton;
    private Button browseProfilesButton;
    private Button browseImagesButton;
    private Button browseNotifsButton;

    // Button for deleting the currently selected item (event/user/image/notif).
    private Button deleteButton;

    // "Account" icon that takes the admin to their profile page.
    private ImageView profileIcon;

    // =========================================================================
    // ADAPTERS AND BACKING DATA
    // =========================================================================

    private ArrayAdapter<String> eventAdapter;

    private NotificationArrayAdapter notifAdapter;

    // For events: the actual Event objects in Firestore "events"
    private final ArrayList<Event> eventList = new ArrayList<>();

    /**
     * Display strings for whatever the ListView is showing (events/users/images).
     * - When showing events: "Title - Organizer Name (email)"
     * - When showing users: "User Name (email)"
     * - When showing images: file name such as "poster1.png"
     */
    private final ArrayList<String> eventDisplayList = new ArrayList<>();

    // Firestore document IDs for events. This is used when deleting events.
    private final ArrayList<String> eventDocIds = new ArrayList<>();

    // For users: we only need the document IDs, not full User objects.
    private final ArrayList<User> profileList = new ArrayList<>();
    private final ArrayList<String> userDocIds = new ArrayList<>();

    // For notifications: backing list for Invite objects (could be loaded later).
    private ArrayList<Notification> notifList = new ArrayList<>() ;

    // For images: file names in Firebase Storage folder "event_posters/".
    private final ArrayList<String> imageFileNames = new ArrayList<>();

    // =========================================================================
    // SELECTION STATE
    // =========================================================================

    /**
     * These hold the index of the selected row in the current "mode".
     * They are used by the delete methods to know which item to remove.
     * A value of -1 means "nothing is selected".
     */
    private int selectedImagePosition = -1;
    private Event selectedEvent;
    private User selectedProfile;
    private int selectedEventPosition = -1;
    private int selectedProfilePosition = -1;
    private int selectedNotifPosition = -1;

    // =========================================================================
    // FIREBASE / CONTROLLERS
    // =========================================================================

    private EventController eventController;
    private FirebaseFirestore db;

    /**
     * Enum describing what the ListView is currently displaying.
     * This is used inside click handlers and delete logic to decide
     * which list and which delete method to use.
     */
    enum IsDisplaying {
        EVENT,    // ListView shows events
        PROFILE,  // ListView shows users
        IMAGE,    // ListView shows image file names
        NOTIF,    // ListView shows notifications (Invites)
        NULL      // Nothing / undefined
    }

    private IsDisplaying isDisplaying = IsDisplaying.NULL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_view);

        // Set up Firestore and controller.
        eventController = new EventController();
        db = FirebaseFirestore.getInstance();

        NotificationController notificationController= new NotificationController();

        // Find views.
        profileIcon = findViewById(R.id.accountIcon);
        browseEventsButton = findViewById(R.id.browse_events_button);
        browseProfilesButton = findViewById(R.id.browse_profiles_button);
        browseImagesButton = findViewById(R.id.browse_images_button);
        browseNotifsButton = findViewById(R.id.browse_notifs_button);
        deleteButton = findViewById(R.id.delete_button);
        eventView = findViewById(R.id.eventList);

        // Start with delete button hidden until something is selected.
        deleteButton.setVisibility(View.GONE);

        // Simple text adapter used for events, users, and images.
        // Backed by eventDisplayList.
        eventAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_single_choice,
                eventDisplayList
        );
        eventView.setAdapter(eventAdapter);
        eventView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // ---------------------------------------------------------------------
        // Button: Browse Events
        // ---------------------------------------------------------------------
        browseEventsButton.setOnClickListener(view -> {
            // Switch mode and clear any previous selection.
            isDisplaying = IsDisplaying.EVENT;
            selectedEventPosition = -1;
            deleteButton.setVisibility(View.GONE);
            // Load and display all events.
            loadEvents();
        });

        // ---------------------------------------------------------------------
        // Button: Browse Profiles (Users)
        // ---------------------------------------------------------------------
        browseProfilesButton.setOnClickListener(view -> {
            isDisplaying = IsDisplaying.PROFILE;
            selectedProfilePosition = -1;
            deleteButton.setVisibility(View.GONE);
            // Load and display all users.
            loadUsers();
        });

        // ---------------------------------------------------------------------
        // Button: Browse Images (Firebase Storage)
        // ---------------------------------------------------------------------
        browseImagesButton.setOnClickListener(view -> {
            isDisplaying = IsDisplaying.IMAGE;
            selectedImagePosition = -1;
            deleteButton.setVisibility(View.GONE);
            // Load and display all image file names.
            loadImages();
        });

        // ---------------------------------------------------------------------
        // Button: Browse Notifs (Invites)
        // ---------------------------------------------------------------------
        //Display all system notifications
        browseNotifsButton.setOnClickListener(view -> {
            eventView = findViewById(R.id.eventList);
            notifList = new ArrayList<>();

            notificationController.generateAllNotifications(new NotificationController.OnNotificationsGenerated() {
                @Override
                public void onNotificationsLoaded(ArrayList<Notification> notifications) {
                    notifList = notifications;
                    notifAdapter = new NotificationArrayAdapter(AdminHomeView.this, notifList);
                    eventView.setAdapter(notifAdapter);
                    notifAdapter.notifyDataSetChanged();
                }
            });
            isDisplaying = IsDisplaying.NOTIF;
        });

        // ---------------------------------------------------------------------
        // Handling List Item Clicks
        // ---------------------------------------------------------------------
        eventView.setOnItemClickListener((parent, view, position, id) -> {
            switch (isDisplaying) {
                case EVENT:
                    // When viewing events, remember the selected Event and index.
                    if (position >= 0 && position < eventList.size()) {
                        selectedEvent = eventList.get(position);
                        selectedEventPosition = position;
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case PROFILE:
                    // When viewing users, we only need the index.
                    // The actual document ID is stored in userDocIds[position].
                    if (position >= 0 && position < eventDisplayList.size()) {
                        selectedProfilePosition = position;
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case IMAGE:
                    // When viewing images, remember which file is selected,
                    // and also show a preview dialog for that image.
                    if (position >= 0 && position < imageFileNames.size()) {
                        selectedImagePosition = position;
                        deleteButton.setVisibility(View.VISIBLE);

                        String fileName = imageFileNames.get(position);
                        showImagePreview(fileName);
                    }
                    break;
                case NOTIF:
                    if (position >= 0 && position < notifList.size()) {
                        selectedNotifPosition = position;
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                    break;
                case NULL:
                default:
                    // If we are not in a defined mode, do nothing.
                    deleteButton.setVisibility(View.GONE);
                    break;
            }
        });

        // ---------------------------------------------------------------------
        // Delete Button Click
        // ---------------------------------------------------------------------
        deleteButton.setOnClickListener(view -> {
            switch (isDisplaying) {
                case EVENT:
                    // Delete the selected event document from Firestore.
                    deleteSelectedEvent();
                    break;
                case PROFILE:
                    // Delete the selected user document from Firestore.
                    deleteSelectedUser();
                    break;
                case IMAGE:
                    // Delete the selected image from Firebase Storage.
                    deleteSelectedImage();
                    break;
                case NOTIF:
                    // A delete notification feature could be implemented here.
                    Toast.makeText(this, "Notification deletion not implemented", Toast.LENGTH_SHORT).show();
                    deleteButton.setVisibility(View.GONE);
                    break;
                case NULL:
                default:
                    deleteButton.setVisibility(View.GONE);
                    break;
            }
        });

        // ---------------------------------------------------------------------
        // Profile Icon Click
        // ---------------------------------------------------------------------
        profileIcon.setOnClickListener(view -> {
            // Go to the admin's profile page.
            Intent intent = new Intent(AdminHomeView.this, ProfileView.class);
            startActivity(intent);
        });
    }

    // =========================================================================
    // EVENTS: LOADING AND DELETING
    // =========================================================================

    /**
     * Loads all events from Firestore "events" collection.
     *
     * For each event, we:
     *  1. Add a temporary display string: "Title - loading organizer..."
     *  2. Look up the organizer's user document in "users" using organizerId.
     *  3. Replace the temporary text with:
     *       "Title - Organizer Name (email)"
     *     or:
     *       "Title - Organizer Name"
     */
    private void loadEvents() {
        db.collection("events")
                .get()
                .addOnSuccessListener((QuerySnapshot queryDocumentSnapshots) -> {
                    // Reset event-related data.
                    eventList.clear();
                    eventDocIds.clear();
                    eventDisplayList.clear();
                    eventAdapter.notifyDataSetChanged();

                    // Use text adapter for the ListView.
                    eventView.setAdapter(eventAdapter);
                    eventView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event e = doc.toObject(Event.class);
                        if (e == null) continue;

                        eventList.add(e);
                        eventDocIds.add(doc.getId());

                        String title = e.getTitle();
                        if (title == null || title.isEmpty()) {
                            title = "(no title)";
                        }

                        // Add placeholder text for this event until we fetch organizer info.
                        final int index = eventDisplayList.size();
                        eventDisplayList.add(title + " - loading organizer...");
                        eventAdapter.notifyDataSetChanged();

                        // Read organizer ID from Event object.
                        String organizerId;
                        try {
                            organizerId = e.getOrganizerId();
                        } catch (Exception ex) {
                            organizerId = null;
                        }

                        // If we can't determine the organizer, mark as unknown.
                        if (organizerId == null || organizerId.isEmpty()) {
                            if (index >= 0 && index < eventDisplayList.size()) {
                                eventDisplayList.set(index, title + " - Unknown organizer");
                                eventAdapter.notifyDataSetChanged();
                            }
                            continue;
                        }

                        final String eventTitleFinal = title;
                        final int indexFinal = index;

                        // Now fetch the organizer's user document.
                        db.collection("users")
                                .document(organizerId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String name = userDoc.getString("name");
                                    String email = userDoc.getString("email");

                                    if (name == null || name.isEmpty()) {
                                        name = "Unknown organizer";
                                    }

                                    // Decide whether to show email or not.
                                    String display;
                                    if (email == null) {
                                        // Email is completely missing.
                                        display = eventTitleFinal + " - " + name;
                                    } else {
                                        String trimmed = email.trim();
                                        // Clean parentheses and check if it looks like a real email.
                                        String cleaned = trimmed
                                                .replace("(", "")
                                                .replace(")", "");

                                        if (cleaned.isEmpty()
                                                || cleaned.equalsIgnoreCase("no email")
                                                || cleaned.equalsIgnoreCase("no-email")
                                                || cleaned.equalsIgnoreCase("none")) {
                                            // This email value is basically a placeholder → skip it.
                                            display = eventTitleFinal + " - " + name;
                                        } else {
                                            // Show email in parentheses after the name.
                                            display = eventTitleFinal + " - " + name + " (" + trimmed + ")";
                                        }
                                    }

                                    // Replace the placeholder string at the correct index.
                                    if (indexFinal >= 0 && indexFinal < eventDisplayList.size()) {
                                        eventDisplayList.set(indexFinal, display);
                                        eventAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e1 -> {
                                    // If we fail to load the organizer, fall back to "Unknown organizer".
                                    if (indexFinal >= 0 && indexFinal < eventDisplayList.size()) {
                                        eventDisplayList.set(indexFinal,
                                                eventTitleFinal + " - Unknown organizer");
                                        eventAdapter.notifyDataSetChanged();
                                    }
                                });
                    }

                    // Clear selected event and hide delete button.
                    selectedEvent = null;
                    selectedEventPosition = -1;
                    deleteButton.setVisibility(Button.GONE);

                    Toast.makeText(
                            this,
                            "Loaded " + eventList.size() + " events",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> Toast.makeText(
                        this,
                        "Failed to load events: " + e.getMessage(),
                        Toast.LENGTH_SHORT
                ).show());
    }

    /**
     * Deletes the currently selected event from Firestore and from the local lists.
     */
    private void deleteSelectedEvent() {
        // Make sure we have a valid selection.
        if (selectedEventPosition < 0
                || selectedEventPosition >= eventDocIds.size()
                || selectedEventPosition >= eventDisplayList.size()) {

            Toast.makeText(this, "Please select an event to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        final int positionToRemove = selectedEventPosition;
        String docId = eventDocIds.get(positionToRemove);

        // Delete the document from Firestore.
        db.collection("events")
                .document(docId)
                .delete()
                .addOnSuccessListener(unused -> {
                    // Remove from local lists so the UI matches the database.
                    eventList.remove(positionToRemove);
                    eventDocIds.remove(positionToRemove);
                    eventDisplayList.remove(positionToRemove);
                    eventAdapter.notifyDataSetChanged();

                    selectedEvent = null;
                    selectedEventPosition = -1;
                    deleteButton.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Event deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to delete event: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    // =========================================================================
    // USERS (PROFILES): LOADING AND DELETING
    // =========================================================================

    /**
     * Loads all users from Firestore "users" collection.
     *
     * For each user we build a display string:
     *   "<name> (email)"  if email is present and useful,
     *   "<name>"          otherwise.
     *
     * We also store each user's document ID in userDocIds so that the admin
     * can delete a user by selecting their row and pressing Delete.
     */
    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(query -> {
                    // Clear old user display data and IDs.
                    eventDisplayList.clear();
                    userDocIds.clear();
                    eventAdapter.notifyDataSetChanged();

                    // Use the text adapter for user display.
                    eventView.setAdapter(eventAdapter);
                    eventView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

                    for (DocumentSnapshot doc : query.getDocuments()) {
                        String name = doc.getString("name");
                        String email = doc.getString("email");

                        if (name == null || name.isEmpty()) {
                            name = "Unknown user";
                        }

                        // Check whether email looks usable.
                        boolean hasValidEmail = false;
                        if (email != null) {
                            String trimmed = email.trim();
                            String cleaned = trimmed
                                    .replace("(", "")
                                    .replace(")", "");

                            if (!cleaned.isEmpty()
                                    && !cleaned.equalsIgnoreCase("no email")
                                    && !cleaned.equalsIgnoreCase("no-email")
                                    && !cleaned.equalsIgnoreCase("none")) {
                                hasValidEmail = true;
                            }
                        }

                        String displayText = hasValidEmail
                                ? name + " (" + email + ")"
                                : name;

                        // Store the text and the user document ID in parallel lists.
                        eventDisplayList.add(displayText);
                        userDocIds.add(doc.getId());
                    }

                    eventAdapter.notifyDataSetChanged();
                    selectedProfile = null;
                    selectedProfilePosition = -1;
                    deleteButton.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Loaded " + eventDisplayList.size() + " users",
                            Toast.LENGTH_SHORT
                    ).show();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to load users: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    /**
     * Deletes the selected user from Firestore "users" collection and removes
     * them from eventDisplayList.
     */
    private void deleteSelectedUser() {
        // Make sure a user row is actually selected.
        if (selectedProfilePosition < 0
                || selectedProfilePosition >= userDocIds.size()
                || selectedProfilePosition >= eventDisplayList.size()) {

            Toast.makeText(this, "Please select a user to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = userDocIds.get(selectedProfilePosition);

        // Delete the user document.
        db.collection("users")
                .document(userId)
                .delete()
                .addOnSuccessListener(unused -> {
                    // Remove from local lists.
                    userDocIds.remove(selectedProfilePosition);
                    eventDisplayList.remove(selectedProfilePosition);
                    eventAdapter.notifyDataSetChanged();

                    selectedProfile = null;
                    selectedProfilePosition = -1;
                    deleteButton.setVisibility(View.GONE);

                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Failed to delete user: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    // =========================================================================
    // IMAGES (FIREBASE STORAGE): LOADING, PREVIEWING, DELETING
    // =========================================================================

    /**
     * Loads file names of all images in Firebase Storage folder "event_posters/"
     * and shows them in the ListView.
     */
    private void loadImages() {

        // Clear old items.
        imageFileNames.clear();
        eventDisplayList.clear();
        eventAdapter.notifyDataSetChanged();

        // Use the text adapter for displaying image file names.
        eventView.setAdapter(eventAdapter);
        eventView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference postersRef = storage.getReference().child("event_posters");

        postersRef.listAll()
                .addOnSuccessListener(listResult -> {

                    // Each StorageReference in listResult.getItems() corresponds to a file.
                    for (StorageReference item : listResult.getItems()) {
                        String fileName = item.getName();
                        imageFileNames.add(fileName);
                        eventDisplayList.add(fileName);
                    }

                    eventAdapter.notifyDataSetChanged();
                    selectedImagePosition = -1;
                    deleteButton.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Loaded " + eventDisplayList.size() + " images",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Failed to load images: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    /**
     * Shows a dialog with the image corresponding to the given file name.
     * The image is downloaded as bytes, decoded into a Bitmap, and displayed.
     */
    private void showImagePreview(String fileName) {
        // Create a vertical layout with an ImageView and a ProgressBar.
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(padding, padding, padding, padding);

        // ImageView where we will display the downloaded Bitmap.
        ImageView imageView = new ImageView(this);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // ProgressBar to show while the image is downloading.
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        progressBar.setPadding(0, padding, 0, 0);

        layout.addView(imageView);
        layout.addView(progressBar);

        // Build and show the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(fileName)
                .setView(layout)
                .setPositiveButton("Close", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        // Reference to the specific image file in Storage.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage
                .getReference()
                .child("event_posters")
                .child(fileName);

        // Limit the size of the download (5 MB).
        final long MAX_BYTES = 5 * 1024 * 1024; // 5 MB max

        imageRef.getBytes(MAX_BYTES)
                .addOnSuccessListener(bytes -> {
                    // Hide the progress bar once we have data.
                    progressBar.setVisibility(View.GONE);

                    // Decode the downloaded bytes into a Bitmap and display it.
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(
                                AdminHomeView.this,
                                "Could not decode image",
                                Toast.LENGTH_SHORT
                        ).show();
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    // Download failed – hide the spinner and show an error.
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(
                            AdminHomeView.this,
                            "Failed to load image: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                    dialog.dismiss();
                });
    }

    /**
     * Deletes the selected image from Firebase Storage and removes the file name
     * from the local lists and adapter.
     */
    private void deleteSelectedImage() {
        // Verify that an image row is selected.
        if (selectedImagePosition < 0
                || selectedImagePosition >= imageFileNames.size()
                || selectedImagePosition >= eventDisplayList.size()) {

            Toast.makeText(this, "Please select an image to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = imageFileNames.get(selectedImagePosition);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage
                .getReference()
                .child("event_posters")
                .child(fileName);

        // Delete the file from Firebase Storage.
        imageRef.delete()
                .addOnSuccessListener(unused -> {
                    // Remove from local lists and update the UI.
                    imageFileNames.remove(selectedImagePosition);
                    eventDisplayList.remove(selectedImagePosition);
                    eventAdapter.notifyDataSetChanged();

                    selectedImagePosition = -1;
                    deleteButton.setVisibility(View.GONE);

                    Toast.makeText(
                            this,
                            "Image deleted",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Failed to delete image: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}