package com.example.willowevents.organizer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.icu.util.Output;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.willowevents.R;
import com.example.willowevents.arrayAdapters.UserArrayAdapter;
import com.example.willowevents.model.User;

import com.google.api.Context;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.example.willowevents.model.Entrant;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Objects;

/**
 * This View shows the organizer a specific list of users from their event
 * (for example, the waitlist or invited list)
 */
public class UserListView extends AppCompatActivity {
    //Firestore whereIn() queries only accept at most 10 values at a time
    private static final int WHERE_IN_MAX = 10;

    private ListView userView;
    private UserArrayAdapter userAdapter;
    private Button close;
    private Button download;
    private TextView numberOfEntrants;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String eventId;
    private String listType;
    private ArrayList entrantList;

    // Single list of users for this screen. This is used for all list types (waitlist, enrolled etc...)
    // The type comes from an intent extra
    private final ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        // Wire views
        userView = findViewById(R.id.user_list);
        close = findViewById(R.id.close_button);
        numberOfEntrants = findViewById(R.id.number_people_on_waitlist);
        download = findViewById(R.id.download_button);

        close.setOnClickListener(v -> finish());
        download.setOnClickListener(v -> downloadUserList(users));

        // Read extras
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Missing extras", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        listType = extras.getString("Type");
        eventId = resolveEventId(getIntent());

        if (!Objects.equals(listType, "enrolled")) download.setVisibility(Button.GONE);

        if (eventId == null || listType == null) {
            //Without an eventID or list type, we cant do anything. So return
            Toast.makeText(this, "Missing event ID or list Type", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (listType.equals("waitlist")) {
            removeEntrant();
        } else if (listType.equals("invited")) {
            removeEntrant();
        }

        //show loading text
        numberOfEntrants.setText("Loading " + listType + " ...");

        //Kick off loading
        loadUsersForEventList(eventId, listType);


    }

    public void removeEntrant() {

        Dialog dialog = new Dialog(this);

        // When an item in the list is clicked, show popup to remove that entrant
        userView.setOnItemClickListener((parent, view, position, id) -> {

            // Get the selected user from the list backing this screen
            User selectedUser = users.get(position);

            dialog.setContentView(R.layout.activity_remove_entrant);
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.setCancelable(false);

            TextView userName = dialog.findViewById(R.id.user_name);
            TextView phoneNumber = dialog.findViewById(R.id.user_phone_number);
            TextView email = dialog.findViewById(R.id.user_email);

            // Fill in the dialog fields from the selected user
            userName.setText(selectedUser.getName());
            phoneNumber.setText(selectedUser.getPhoneNumber());
            email.setText(selectedUser.getEmail());

            Button cancelButton = dialog.findViewById(R.id.cancel_button);
            Button removalButton = dialog.findViewById(R.id.remove_button);

            // Cancel just closes the dialog
            cancelButton.setOnClickListener(v -> dialog.dismiss());

            // Remove entrant from Firestore and from the local list
            removalButton.setOnClickListener(v -> {

                // Map current listType ("waitlist", "invited", etc.) to Firestore field
                String field = mapTypeToField(listType);

                if (eventId == null || field == null) {
                    Toast.makeText(
                            UserListView.this,
                            "Missing event ID or list type",
                            Toast.LENGTH_SHORT
                    ).show();
                    dialog.dismiss();
                    return;
                }

                // Assuming your User / Entrant model has getId()
                String userId = selectedUser.getID();
                if (userId == null || userId.isEmpty()) {
                    Toast.makeText(
                            UserListView.this,
                            "User has no ID; cannot remove.",
                            Toast.LENGTH_SHORT
                    ).show();
                    dialog.dismiss();
                    return;
                }

                // Remove the userId from the corresponding array field on the event document
                db.collection("events")
                        .document(eventId)
                        .update(field, FieldValue.arrayRemove(userId))
                        .addOnSuccessListener(aVoid -> {
                            // Update local list + adapter
                            users.remove(position);
                            userAdapter.notifyDataSetChanged();

                            String label = "Number of users on " + listType + " : " + users.size();
                            numberOfEntrants.setText(label);

                            Toast.makeText(
                                    UserListView.this,
                                    "Removed " + selectedUser.getName() + " from " + listType,
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(
                                    UserListView.this,
                                    "Failed to remove entrant: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                            dialog.dismiss();
                        });
            });

            dialog.show();
        });
    }

    /**
     * Fetch the event and extract the correct array of user IDs.
     */
    private void loadUsersForEventList(String eventId, String listType) {
        //convert the list type into the firestore field name
        String field = mapTypeToField(listType);

        if (field == null) {
            // We don't recognize this list type, return
            Toast.makeText(this, "Unknown list type: " + listType, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Read the event doc from the "event" collection on firestore
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || !snap.exists()) {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    // Read the array of user IDs
                    List<String> idsRaw = (List<String>) snap.get(field);
                    List<String> ids = idsRaw != null ? idsRaw : new ArrayList<>();

                    if (ids.isEmpty()) {
                        // No users in this list, update UI
                        users.clear();
                        updateUIAfterLoad();
                        return;
                    }

                    //fetch user documents for these IDs
                    fetchUsersByIds(ids);
                })
                .addOnFailureListener(e -> {
                    //Could not load the event doc
                    Toast.makeText(this, "Failed to load event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    /**
     * fetch the User docs in batches (because whereIn max is 10, we need a way to handle more).
     */
    private void fetchUsersByIds(List<String> ids) {
        //Clear any previous data
        users.clear();
        //Split IDs into smaller lists with sizes smaller or equal to 10
        List<List<String>> batches = chunk(ids, WHERE_IN_MAX);
        // Process each batch recursively
        fetchBatchRecursive(batches, 0);
    }

    private void fetchBatchRecursive(List<List<String>> batches, int index) {
        //Batches is a list of lists where the inner list contains <= 10 IDs
        //index is which batch is being processed
        if (index >= batches.size()) {
            // All batches done, we can update the UI
            updateUIAfterLoad();
            return;
        }

        List<String> batch = batches.get(index);
        //Query the firestore collection "Users" for all docs whose id is in this batch
        db.collection("users")
                .whereIn("id", batch)
                .get()
                .addOnSuccessListener(qs -> {
                    // Turn each DocSnapshot into a user
                    for (com.google.firebase.firestore.DocumentSnapshot d : qs.getDocuments()) {
                        User u = docToUser(d);
                        if (u != null) {
                            users.add(u);
                        }
                    }
                    // Go to next batch
                    fetchBatchRecursive(batches, index + 1);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load users: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    // Still try remaining batches
                    fetchBatchRecursive(batches, index + 1);
                });
    }

    /**
     * After all users are loaded into the list, we can update the the ListView Adapter and the UI
     */
    private void updateUIAfterLoad() {
        if (userAdapter == null) {
            //userAdapter is null on the first time loading
            userAdapter = new UserArrayAdapter(this, users);
            userView.setAdapter(userAdapter);
        } else {
            userAdapter.clear();
            userAdapter.addAll(users);
            userAdapter.notifyDataSetChanged();
        }
        //Show the number of users at the top
        String label = "Number of users on " + listType + " : " + users.size();
        numberOfEntrants.setText(label);
    }

    /**
     * Helper function to map an internal type string ("waitlist", "invited", etc.)
     * to the corresponding field name on the Firestore event document.
     */
    static String mapTypeToField(String type) {
        if (type == null) {
            return null;
        }
        switch (type) {
            case "waitlist":
                return "waitlist";
            case "invited":
                return "inviteList";
            case "enrolled":
                return "approvedList";
            case "cancelled":
                return "cancelledList";
            default:
                return null;
        }
    }

    /**
     * Convert a Firestore DocumentSnapshot into our Entrant model.
     * <p>
     * This method:
     * - Reads basic fields
     * - Uses 'safe()' to protect against nulls.
     * - Creates an Entrant instance (which implements User).
     * <p>
     * If the document doesn't exist, we return null to signal "no user".
     */
    private User docToUser(com.google.firebase.firestore.DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;

        String id = safe(doc.getString("id"));
        String name = safe(doc.getString("name"));
        String email = safe(doc.getString("email"));
        String phone = safe(doc.getString("phoneNumber"));

        return new com.example.willowevents.model.Entrant(
                id,
                name,
                email,
                phone,
                new ArrayList<>()
        );
    }

    /**
     * Small helper to convert null Strings into empty strings.
     * This avoids having to constantly do null checks in UI code
     */
    static String safe(String s) {
        return s == null ? "" : s;
    }

    /**
     * Split a list into smaller lists of at most 'size' elements.
     * Example:
     * src = [a, b, c, d, e], size = 2
     * -> [[a, b], [c, d], [e]]
     * Useful because firestore whereIn() takes in at most 10 values
     */

    static List<List<String>> chunk(List<String> src, int size) {
        List<List<String>> out = new ArrayList<>();
        for (int i = 0; i < src.size(); i += size) {
            out.add(src.subList(i, Math.min(src.size(), i + size)));
        }
        return out;
    }

    private static String resolveEventId(android.content.Intent intent) {
        if (intent == null) return null;
        //because we are uncoordinated and have used different names lol
        String id = intent.getStringExtra("Event ID");
        if (id == null) id = intent.getStringExtra("event ID");
        if (id == null) id = intent.getStringExtra("eventId");
        if (id == null) id = intent.getStringExtra("EXTRA_EVENT_ID");
        return id;
    }


    private void downloadUserList(ArrayList<User> userList) {
//        https://stackoverflow.com/questions/27772011/how-to-export-data-to-csv-file-in-android
//        https://stackoverflow.com/questions/64966826/android-storing-files-in-downloads
        String filename = "users_in_" + listType + ".csv";

        ContentValues contentValues = new ContentValues();
        ContentResolver contentResolver = UserListView.this.getContentResolver();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, filename);

        Uri uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);

                // MUST CATCH EXCEPTIONS FOR DEPRECATED API
        if (uri != null) {
            try (OutputStream out = contentResolver.openOutputStream(uri)) {
                if (out != null) {
                    // Write header
                    out.write("name,email,phoneNumber\n".getBytes());

                    // Example rows
                    for (User user : userList) {
                        String row = user.getName() + "," + user.getEmail() + "," + user.getPhoneNumber() + "\n";
                        out.write(row.getBytes());
                    }

                    out.flush();
                    Toast.makeText(this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}