package com.example.willowevents;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class JoinWaitlist {

    private final FirebaseFirestore db;

    public JoinWaitlist() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface JoinWaitlistCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void execute(String eventId, String entrantId, JoinWaitlistCallback callback) {
        db.collection("events")
                .document(eventId)
                .update("waitlist", FieldValue.arrayUnion(entrantId))
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}