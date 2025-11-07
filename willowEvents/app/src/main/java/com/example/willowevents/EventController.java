package com.example.willowevents;

import android.util.Log;

import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventController {
    private FirebaseFirestore eventDB;
    private CollectionReference eventsRef;
    // Author: Jeanard Sinfuego

    // get all users
    public EventController() {
            // get pointer to database
            eventDB = FirebaseFirestore.getInstance();
            // get all users
            eventsRef = eventDB.collection("events");

    }

    /**     generates snapshot of the AVAILABLE events form the database
     *
     */
    public void generateAllEvents(OnEventsGeneration callback) {
        eventsRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            else {
                ArrayList<Event> events = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot: value){
                    Event event = snapshot.toObject(Event.class);
//                    event.setId(snapshot.getId());
                    events.add(event);
                }
                callback.onEventsGenerated(events);
            }

        });
    }

    /**     generates a single snapshot of the AVAILABLE events form the database
     *
     */
    public void generateOneEvent(String eventID, OnEventGeneration callback) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnSuccessListener(document -> {

            Event event = null;

            // if user exists
            if (document.exists()) {
                event = document.toObject(Event.class);
            }
            callback.onEventGenerated(event);

        });
    }

    public void retrieveCapacity(String eventID, OnExistsCapacity callback) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnSuccessListener(document -> {

            Long waitlistLimit = null;
            if (document.exists()) {
                 waitlistLimit = document.getLong("waitlistLimit");
            }
            callback.getCapacity(waitlistLimit);
        });
    }
    // ------------- UPDATING EVENT
    public void updateEventInfo(Event event ) {
        tempDeleteEvent(event);
        reAddEvent(event);
    }

    /**
     * Private method meant to re-add a event after deleting. To be used only with updateEventInfo
     */
    private void reAddEvent(Event event) {
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.set(event);
    }

    /**
     * Private method meant to TEMPORARILY delete an existing event after updating. to be used onlw with updateEventInfo
     */
    private void tempDeleteEvent(Event event) {
        DocumentReference docRef = eventsRef.document(event.getId());
        docRef.delete();


    }
    
    
    // ------------- ADDING FUNCTIONALITY
    /**
     * Adds user to the waitlist
     * @param eventID the ID of the event
     * @param userID the ID of the user
     */
    public void addUserWaitlist(String eventID, String userID) {
        eventsRef.document(eventID).update("waitlist", FieldValue.arrayUnion(userID));
    }

    /**
     * Adds user to the inviteList
     * @param eventID
     */
    public void addUserInvitelist(String eventID, String userID) {
        eventsRef.document(eventID).update("inviteList", FieldValue.arrayUnion(userID));
    }

    /**
     * Adds user to the registeredList
     * @param userID the ID of the user
     * @param eventID the ID of the event
     *
     */
    public void addUserRegisteredList(String eventID, String userID) {
        eventsRef.document(eventID).update("approvedList", FieldValue.arrayUnion(userID));
    }

    /**
     * Adds user to the cancelled list
     * @param userID the ID of the user
     * @param eventID the ID of the event
     */
    public void addUserCancelledList(String eventID, String userID) {
        eventsRef.document(eventID).update("cancelledList", FieldValue.arrayUnion(userID));
    }
    //---------------- DELETING FUNCTIONALITY
    /**
     * Removes user from the waitlist
     * @param eventID the ID of the event
     * @param userID the ID of the user
     */
    public void removeUserWaitlist(String eventID, String userID) {
        eventsRef.document(eventID).update("waitlist", FieldValue.arrayRemove(userID));
    }

    /**
     * Removes user from the invite list
     * @param eventID the ID of the event
     * @param userID the ID of the user
     */
    public void removeUserInviteList(String eventID, String userID) {
        eventsRef.document(eventID).update("inviteList", FieldValue.arrayRemove(userID));
    }

    /**
     * Removes user from the registered list
     * @param eventID the ID of the event
     * @param userID the ID of the user
     */
    public void removeUserRegisteredList(String eventID, String userID) {
        eventsRef.document(eventID).update("approvedList", FieldValue.arrayRemove(userID));
    }

    /**
     * Removes user from the cancelled list.
     *
     * @param eventID the ID of the event
     * @param userID the ID of the user
     */
    public void removeUserCancelledList(String eventID, String userID) {
        eventsRef.document(eventID).update("cancelledList", FieldValue.arrayRemove(userID));
    }

    //---------------- EXISTS-IN-ARRAY FUNCTIONALITY

    /**
     * Checks if a user exists in as given array
     * @param eventID the string of the device ID of the user
     */
    public void existsInGivenArray(String eventID, String arrayName, String user) {

    }





    public interface OnExistsCapacity {
        void getCapacity(Long capacity);
    }
    public interface ExistsInArray {

    void existsInArray(boolean contains);
    }

    public interface OnEventsGeneration {
        void onEventsGenerated(ArrayList<Event> events);
    }

    public interface OnEventGeneration {
        void onEventGenerated(Event event);
    }
}






