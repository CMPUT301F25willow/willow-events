package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.R;
import com.example.willowevents.UserArrayAdapter;
import com.example.willowevents.model.User;
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

    private ListView userView;
    private UserArrayAdapter userAdapter;
    private static final int WHERE_IN_MAX = 10;
    private Button close;
    private TextView numberOfEntrants;
    private ArrayList<User> waitlistUsers;
    private ArrayList<User> invitedUsers;
    private ArrayList<User> enrolledUsers;
    private ArrayList<User> cancelledUsers;
    String type = "";
    private final com.google.firebase.firestore.FirebaseFirestore db =
            com.google.firebase.firestore.FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        //String eventId = null;

        Bundle extras = getIntent().getExtras();
        logExtras(extras);
        String eventId = resolveEventId(getIntent());
        String type = (extras != null) ? extras.getString("Type") : null;

        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(this, "Missing Event ID", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        //REPLACE WITH FIRESTORE STUFF
        waitlistUsers = new ArrayList<User>();
        invitedUsers = new ArrayList<User>();
        enrolledUsers = new ArrayList<User>();
        cancelledUsers = new ArrayList<User>();

        loadAllEntrantLists(eventId);

        ArrayList<String> tempList = new ArrayList<>();


//        waitlistUsers.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
//        waitlistUsers.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
//        waitlistUsers.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
//        waitlistUsers.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));
//
//        invitedUsers.add(new Entrant("0", "Alfredo", "email@gmail.com", "306 123 456", tempList));
//        invitedUsers.add(new Entrant("1", "Bernadice", "email@gmail.com", "306 123 456", tempList));
//        invitedUsers.add(new Entrant("2", "Claudelia", "email@gmail.com", "306 123 456", tempList));
//        invitedUsers.add(new Entrant("3", "Domingo", "email@gmail.com", "306 123 456", tempList));
//
//        enrolledUsers.add(new Entrant("0", "Albert", "email@gmail.com", "306 123 456", tempList));
//        enrolledUsers.add(new Entrant("1", "Bertrand", "email@gmail.com", "306 123 456", tempList));
//        enrolledUsers.add(new Entrant("2", "Cornelius", "email@gmail.com", "306 123 456", tempList));
//        enrolledUsers.add(new Entrant("3", "Daedalus", "email@gmail.com", "306 123 456", tempList));
//
//        cancelledUsers.add(new Entrant("0", "Aurora", "email@gmail.com", "306 123 456", tempList));
//        cancelledUsers.add(new Entrant("1", "Belle", "email@gmail.com", "306 123 456", tempList));
//        cancelledUsers.add(new Entrant("2", "Cinderella", "email@gmail.com", "306 123 456", tempList));
//        cancelledUsers.add(new Entrant("3", "Diana", "email@gmail.com", "306 123 456", tempList));


        //Get elements
        close = findViewById(R.id.close_button);
        numberOfEntrants = findViewById(R.id.number_people_on_waitlist);

        //Go back on close
        close.setOnClickListener(view -> {
            Intent myIntent = new Intent(UserListView.this, EventOrganizerEntrantView.class);
            startActivity(myIntent);
        });

        userView = findViewById(R.id.user_list);
        int size;
        if (Objects.equals(type, "waitlist")) {
            size = waitlistUsers.size();
            userAdapter = new UserArrayAdapter(this, waitlistUsers);
        } else if (Objects.equals(type, "invited")) {
            size = invitedUsers.size();
            userAdapter = new UserArrayAdapter(this, invitedUsers);
        } else if (Objects.equals(type, "enrolled")) {
            size = enrolledUsers.size();
            userAdapter = new UserArrayAdapter(this, enrolledUsers);
        } else if (Objects.equals(type, "cancelled")) {
            size = cancelledUsers.size();
            userAdapter = new UserArrayAdapter(this, cancelledUsers);
        } else {
            //smth bad happened
            size = 0;
        }
        String numberEntrantsMessage = "Number of users on waitlist : " + size;
        numberOfEntrants.setText(numberEntrantsMessage);

        userView.setAdapter(userAdapter);     //link array adapter to ListView
    }

    private void loadAllEntrantLists(String eventId) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(snap -> {
                    if (snap == null || !snap.exists()) {
                        toast("Event not found");
                        return;
                    }

                    java.util.List<String> waitIds = extractStringArray(snap, "waitlist");
                    java.util.List<String> inviteIds = extractStringArray(snap, "inviteList");
                    java.util.List<String> approvedIds = extractStringArray(snap, "approvedList");
                    java.util.List<String> cancelledIds = extractStringArray(snap, "cancelledList");

                    // Load each list (in parallel). Each method clears and refills its target list.
                    fetchUsersIntoList(waitIds, waitlistUsers, () -> notifyIfShowing("waitlist"));
                    fetchUsersIntoList(inviteIds, invitedUsers, () -> notifyIfShowing("invited"));
                    fetchUsersIntoList(approvedIds, enrolledUsers, () -> notifyIfShowing("enrolled"));
                    fetchUsersIntoList(cancelledIds, cancelledUsers, () -> notifyIfShowing("cancelled"));

                })
                .addOnFailureListener(e -> toast("Failed to load event: " + e.getMessage()));
    }

    private java.util.List<String> extractStringArray(com.google.firebase.firestore.DocumentSnapshot snap, String field) {
        Object raw = snap.get(field);
        if (!(raw instanceof java.util.List<?>)) return java.util.Collections.emptyList();
        java.util.ArrayList<String> out = new java.util.ArrayList<>();
        for (Object o : (java.util.List<?>) raw) {
            if (o instanceof String) out.add((String) o);
        }
        return out;
    }

    private void fetchUsersIntoList(java.util.List<String> uids,
                                    java.util.ArrayList<com.example.willowevents.model.User> targetList,
                                    java.lang.Runnable onComplete) {

        targetList.clear();
        if (uids == null || uids.isEmpty()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        java.util.List<java.util.List<String>> batches = chunk(uids, WHERE_IN_MAX);
        fetchBatchRecursive(batches, 0, targetList, onComplete);
    }

    private void fetchBatchRecursive(java.util.List<java.util.List<String>> batches, int idx,
                                     java.util.ArrayList<com.example.willowevents.model.User> targetList,
                                     java.lang.Runnable onComplete) {
        if (idx >= batches.size()) {
            if (onComplete != null) onComplete.run();
            return;
        }

        java.util.List<String> batch = batches.get(idx);
        db.collection("users").whereIn("id", batch).get()
                .addOnSuccessListener(qs -> {
                    for (com.google.firebase.firestore.DocumentSnapshot d : qs.getDocuments()) {
                        com.example.willowevents.model.User u = toEntrantFromDoc(d);
                        if (u != null) targetList.add(u);
                    }
                    fetchBatchRecursive(batches, idx + 1, targetList, onComplete);
                })
                .addOnFailureListener(e -> {
                    toast("Failed to load users: " + e.getMessage());
                    // You can continue or stop; we’ll continue to try the next batches:
                    fetchBatchRecursive(batches, idx + 1, targetList, onComplete);
                });
    }

    private com.example.willowevents.model.User toEntrantFromDoc(com.google.firebase.firestore.DocumentSnapshot doc) {
        if (doc == null || !doc.exists()) return null;

        String id = str(doc.getString("id"));
        String name = str(doc.getString("name"));
        String email = str(doc.getString("email"));
        String phone = str(doc.getString("phoneNumber"));

        // Your Entrant constructor needs a joinList. Use empty list unless you have real data.
        java.util.ArrayList<String> joinList = new java.util.ArrayList<>();
        return new com.example.willowevents.model.Entrant(id, name, email, phone, joinList);
    }


    private static String str(String s) {
        return s == null ? "" : s;
    }

    private java.util.List<java.util.List<String>> chunk(java.util.List<String> src, int size) {
        java.util.ArrayList<java.util.List<String>> out = new java.util.ArrayList<>();
        for (int i = 0; i < src.size(); i += size) {
            out.add(src.subList(i, Math.min(src.size(), i + size)));
        }
        return out;
    }

    private void toast(String msg) {
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_LONG).show();
    }

    private void notifyIfShowing(String which) {
    }

    private void logExtras(android.os.Bundle extras) {
        if (extras == null) {
            android.util.Log.d("UserListView", "extras = null");
            return;
        }
        for (String k : extras.keySet()) {
            Object v = extras.get(k);
            android.util.Log.d("UserListView", "extra: " + k + " = " + v);
        }
    }

    private static String resolveEventId(android.content.Intent intent) {
        if (intent == null) return null;
        // Try all variants your project has used:
        String id = intent.getStringExtra("Event ID");
        if (id == null) id = intent.getStringExtra("event ID");
        if (id == null) id = intent.getStringExtra("eventId");
        if (id == null) id = intent.getStringExtra("EXTRA_EVENT_ID");
        return id;
    }
}