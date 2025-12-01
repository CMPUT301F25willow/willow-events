package com.example.willowevents.controller;

import android.util.Log;

import com.example.willowevents.model.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

            // if event exists
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


    /**
     * This filters events based on the provided tags and dates
     * Tags and dates can be null. In that case, all events will be included
     * @param preferences a list of tags to be compared with the event description and title
     * @param from The earliest date INCLUSIVE to search for an event end. ASSUME FROM DATE IS NOT AFTER TO DATE
     * @param to The latest date INCLUSIVE to search for event start ASSUME TO DATE IS NOT BEFORE FROM DATE
     * @param callback is the callback function for the Activity to implement once event has been generated
     */
    public void filterEvents(List<String> preferences, Date from, Date to, OnEventsGeneration callback) {
        Query q = eventsRef;

        if (from != null) {
            q = q.whereGreaterThanOrEqualTo("eventDate", from);
        }


        if (to != null) {

            q = q.whereLessThanOrEqualTo("eventDate", to);
        }

        // AFTER DATE FILTERING, TAG FILTER NEXT
        q.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<Event> events = new ArrayList<>();

            for (DocumentSnapshot eventDoc : queryDocumentSnapshots) {
                // get event
                Event event = eventDoc.toObject(Event.class);

                if (!preferences.isEmpty()) {    // CASE 1: TAGS ARE SELECTED THEN NARROW DOWN
                    String title = event.getTitle().toLowerCase();
                    String description = event.getDescription().toLowerCase();

                    for (String preference: preferences) {
                        String preferenceLower = preference.toLowerCase();

                        // IF TAG MATCHES THEN SHOW EVENT
                        if (description.contains(preferenceLower) || title.contains(preferenceLower)) {

                            events.add(event);

                            // break inner for loop for preferences already matching
                            break;
                        }
                    }
                } else {             // CASE 2: NO TAGS SELECTED THEN INCLUDE EVERYTHING
                    events.add(event);
                }
            }
            // AFTER TAG FILTERING, CALLBACK
            callback.onEventsGenerated(events);
        });
    }
    // ------------- UPDATING EVENT
    public void updateEventInfo(Event event ) {
        eventsRef.document(eventsRef.getId()).set(event);
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
     * Removes the event from the database
     * @param eventID String of the event ID
     */
    public void removeEvent(String eventID) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.delete();
    }
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
    public List<Event> filterAvailable(List<Event> all) {
        List<Event> out = new ArrayList<>();
        if (all == null) return out;

        for (Event e : all) {
            if (e == null) continue;
            Integer limit = e.getInvitelistlimit(); // null => unlimited
            if (limit == null) { out.add(e); continue; }   // unlimited => include
            if (limit == 0) { continue; }                 // ignore zero-capacity

            List<String> inviteList = e.getInviteList();
            int count = (inviteList == null) ? 0 : inviteList.size();

            if (count < limit) {                          // strictly less than
                out.add(e);
            }
        }
        return out;
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






