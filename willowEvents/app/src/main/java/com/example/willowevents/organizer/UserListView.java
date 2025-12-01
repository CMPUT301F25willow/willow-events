package com.example.willowevents.organizer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.arrayAdapters.UserArrayAdapter;
import com.example.willowevents.model.User;

import com.google.firebase.firestore.FirebaseFirestore;

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

        close.setOnClickListener(v -> finish());

        // Read extras
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Toast.makeText(this, "Missing extras", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        listType = extras.getString("Type");
        eventId = resolveEventId(getIntent());

        if (eventId == null || listType == null) {
            //Without an eventID or list type, we cant do anything. So return
            Toast.makeText(this, "Missing event ID or list Type", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (listType.equals("waitlist")){
            removeEntrant();
        }

        //show loading text
        numberOfEntrants.setText("Loading " + listType + " ...");

        //Kick off loading
        loadUsersForEventList(eventId, listType);



    }

    public void removeEntrant(){

        Dialog dialog = new Dialog(this);

        // Upon clicking an item in list, get popup to remove entrant from list
        // Pop up should work in theory, cannot confirm
        userView.setOnItemClickListener((parent, view, position, id) -> {

            dialog.setContentView(R.layout.activity_remove_entrant);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            TextView userName = dialog.findViewById(R.id.user_name);
            TextView phoneNumber = dialog.findViewById(R.id.user_phone_number);
            TextView email = dialog.findViewById(R.id.user_email);

            //userName.setText(waitlist.get(position).getName());
            //phoneNumber.setText(waitlist.get(position).getPhoneNumber());
            //email.setText(waitlist.get(position).getEmail());

            Button cancelButton = dialog.findViewById(R.id.cancel_button);
            Button removalButton = dialog.findViewById(R.id.remove_button);

            // click on cancel button to leave fragment
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            // remove entrant from list, then leave fragment
            removalButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get this entrant, remove them from waitlist of event
                    // IDK HOW TO FIREBASE THIS I TRIED

                    dialog.dismiss();
                }
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
        if (type == null){
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
     *
     * This method:
     *  - Reads basic fields
     *  - Uses 'safe()' to protect against nulls.
     *  - Creates an Entrant instance (which implements User).
     *
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
     *  src = [a, b, c, d, e], size = 2
     *  -> [[a, b], [c, d], [e]]
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

    private void downloadUserList(){

    }
}