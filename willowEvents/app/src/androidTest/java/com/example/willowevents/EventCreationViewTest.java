package com.example.willowevents;


import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(AndroidJUnit4.class)
public class EventCreationViewTest {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // We’ll write into a dedicated test collection to avoid polluting real data
    private static final String TEST_COLL = "events_test";
    private final List<DocumentReference> createdDocs = new ArrayList<>();

    @Before
    public void signIn() throws Exception {
        // Requires Anonymous sign-in enabled in your Firebase Console
        Tasks.await(auth.signInAnonymously());
        assertNotNull("Auth user must be non-null", auth.getCurrentUser());
    }

    @After
    public void cleanUp() {
        // Best-effort cleanup of created docs
        for (DocumentReference ref : createdDocs) {
            ref.delete();
        }
    }

    @Test
    public void writeAndReadEvent_minimal() throws Exception {
        String id = UUID.randomUUID().toString();
        DocumentReference ref = db.collection(TEST_COLL).document(id);

        Map<String, Object> event = new HashMap<>();
        event.put("id", id);
        event.put("title", "Direct Test Event");
        event.put("description", "Created by instrumented test");
        Date now = new Date();
        event.put("registrationOpenDate", now);
        event.put("registrationCloseDate", new Date(now.getTime() + 86_400_000)); // +1 day
        event.put("eventDate", new Date(now.getTime() + 2 * 86_400_000)); // +2 days
        event.put("organizerId", auth.getCurrentUser().getUid());
        event.put("waitlist", Arrays.asList("user-001", "user-002"));
        event.put("inviteList", Collections.emptyList());
        event.put("approvedList", Collections.emptyList());
        event.put("cancelledList", Collections.emptyList());
        // optional fields only if you use them:
        // event.put("waitlistLimit", 10);
        // event.put("capacity", 100);

        // Write
        Tasks.await(ref.set(event));
        createdDocs.add(ref);

        // Read back
        var snapshot = Tasks.await(ref.get());
        assertTrue(snapshot.exists());
        assertEquals("Direct Test Event", snapshot.getString("title"));
        assertEquals(auth.getCurrentUser().getUid(), snapshot.getString("organizerId"));

        // Waitlist should be a list of strings
        @SuppressWarnings("unchecked")
        List<String> waitlist = (List<String>) snapshot.get("waitlist");
        assertNotNull(waitlist);
        assertTrue(waitlist.contains("user-001"));
        assertTrue(waitlist.contains("user-002"));
    }

    @Test
    public void updateInvite_thenApprove() throws Exception {
        String id = UUID.randomUUID().toString();
        DocumentReference ref = db.collection(TEST_COLL).document(id);

        Map<String, Object> base = new HashMap<>();
        base.put("id", id);
        base.put("title", "Invite Flow");
        base.put("description", "Test invite/approve flow");
        base.put("eventDate", new Date(System.currentTimeMillis() + 2 * 86_400_000));
        base.put("registrationOpenDate", new Date());
        base.put("registrationCloseDate", new Date(System.currentTimeMillis() + 86_400_000));
        base.put("organizerId", auth.getCurrentUser().getUid());
        base.put("waitlist", Arrays.asList("u1", "u2", "u3"));
        base.put("inviteList", new ArrayList<String>());
        base.put("approvedList", new ArrayList<String>());
        base.put("cancelledList", new ArrayList<String>());

        Tasks.await(ref.set(base));
        createdDocs.add(ref);

        // Add invite
        Tasks.await(ref.update("inviteList", Arrays.asList("u1", "u2")));

        // Accept u1: inviteList remove u1, approvedList add u1
        // (This is a client-side simulation; in real app you'd do it in code or with a Cloud Function)
        var snap = Tasks.await(ref.get());
        @SuppressWarnings("unchecked") List<String> invite = (List<String>) snap.get("inviteList");
        @SuppressWarnings("unchecked") List<String> approved = (List<String>) snap.get("approvedList");
        if (invite == null) invite = new ArrayList<>();
        if (approved == null) approved = new ArrayList<>();
        invite.remove("u1");
        if (!approved.contains("u1")) approved.add("u1");

        Map<String, Object> updates = new HashMap<>();
        updates.put("inviteList", invite);
        updates.put("approvedList", approved);
        Tasks.await(ref.update(updates));

        // Verify u1 is approved
        var snap2 = Tasks.await(ref.get());
        @SuppressWarnings("unchecked") List<String> approved2 = (List<String>) snap2.get("approvedList");
        assertTrue(approved2.contains("u1"));
    }
}


