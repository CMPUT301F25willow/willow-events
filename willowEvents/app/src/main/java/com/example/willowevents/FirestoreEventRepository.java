package com.example.willowevents;

import com.example.willowevents.model.Event;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Map;
import java.util.HashMap;


public final class FirestoreEventRepository implements EventRepository {
    private final FirebaseFirestore db;


    public FirestoreEventRepository(FirebaseFirestore db) { this.db = db; }


    @Override
    public void createEvent(Event event, StringCallback cb) {
        DocumentReference doc = db.collection("events").document(); // events/{eventId}
        String eventId = doc.getId();
        Map<String, Object> map = EventMapper.toMap(event, eventId);
        doc.set(map)
                .addOnSuccessListener(v -> cb.onSuccess(eventId))
                .addOnFailureListener(cb::onError);
    }


    @Override
    public void attachPoster(String eventId, String posterUrl, VoidCallback cb) {
        db.collection("events").document(eventId)
                .update("posterUrl", posterUrl)
                .addOnSuccessListener(v -> cb.onSuccess())
                .addOnFailureListener(cb::onError);
    }
}
