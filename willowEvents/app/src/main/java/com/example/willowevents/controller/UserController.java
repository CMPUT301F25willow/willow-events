package com.example.willowevents.controller;

import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Organizer;
import com.example.willowevents.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class UserController {
    // Author: Jeanard Sinfuego
    // TODO: edge cases when user profile gets deleted and therefore must delete from events and waitlist
    private FirebaseFirestore userDB;

    // get all users
    private CollectionReference usersRef;
    public UserController() {
        // get pointer to database
        userDB= FirebaseFirestore.getInstance();
        // get all users
        usersRef = userDB.collection("users");
    }

    /**
     * Adds a user to the database given an entrnat. Gives it a unique tag "Entrant" in the database
     * @param userID is the Device-ID as a STRING of the user
     */
    public void addNewEntrantUser(String userID) {

        Entrant user = new Entrant(userID,
                "No name",
                "No email",
                "No phone number",
                new ArrayList<>());

        DocumentReference docRef = usersRef.document(userID);
        docRef.set(user);
    }

    /**
     * Adds a user to the database given an organizer. Gives it a unique tag "Entrant" in the database
     * @param userID is the Device-ID as a STRING of the user
     */
    public void addNewOrganizerUser(String userID) {
        Organizer user = new Organizer(userID,
                "No name",
                "No email",
                "No phone number",
                new ArrayList<>());

        DocumentReference docRef = usersRef.document(userID);
        docRef.set(user);
    }

    /**
     * Adds a user to the database given an ADMIN
     */
    public void addAdminUser(String userID) {
        // TO BE IMPLEMENTED IN FUTURE ITERATION

    }

    /**
     * Given a userID in the form of a String, return the user details from the database
     * User can only be retrieved via a callback function
     * @param deviceID is the Device-ID as a STRING of the user
     *
     */
    public void getUser(String deviceID , OnUserLoaded callback) {
        // get document via id
        DocumentReference docRef = usersRef.document(deviceID);
        // https://firebase.google.com/docs/firestore/query-data/get-data?hl=fr#java
        docRef.get().addOnSuccessListener(document -> {

            User user = null;

            // if user exists
            if (document.exists()) {
                    String userType = document.getString("userType");
                    // user is an entrant
                    if (Objects.equals(userType, "organizer")) {
                        user = document.toObject(Organizer.class);
                    }
                    // user is an entrant
                    else {
                        user = document.toObject(Entrant.class);
                    }
                callback.onUserLoaded(user);
            }
            else{
                callback.onUserLoaded(null);
            }


            // for debugging in case
        }).addOnFailureListener(error -> {
            error.printStackTrace();
        });
    }

    /***
     * Checks if a given user exists in the database queried by the String of its device ID
     * @param deviceID String of the device ID
     * @return True if the user is registered in the database. Else return False.
     */
    public void userExists(String deviceID, OnExistsUser callback) {
        usersRef.document(deviceID).get().addOnSuccessListener( document -> {

            User user = null;
            if (document.exists()) {
                String userType = document.getString("userType");
                if (Objects.equals(userType, "organizer")) {
                    user = document.toObject(Organizer.class);
                }
                else {
                    user = document.toObject(Entrant.class);
                }

            }
            callback.onExistsUser(document.exists(), user);

        });
    }


    /**
     * Updates the user info given
     * @param user The most recently updated object of the user to store in the database
     */
    public void updateUserInfo(User user ) {
        tempDeleteUser(user);
        reAddUser(user);

    }

    /**
     * Private method meant to re-add a user after deleting. To be used only with updateUserInfo
     */
    private void reAddUser(User user) {
        DocumentReference docRef = usersRef.document(user.getID());
        docRef.set(user);
    }

    /**
     * Private method meant to TEMPORARILY delete an existing user after updating. to be used onlw with updateUserInfo
     */
    private void tempDeleteUser(User user) {
        DocumentReference docRef = usersRef.document(user.getID());
        docRef.delete();


    }

    /**
     * Private method to retrieve organizer class. Assumption is that Organizer already exists
     * @param document The Document snapshot of the user ID query
     * @return The placeholder of organizer
     */
    private Organizer generateOrganizer(DocumentSnapshot document, String deviceID) {
        String name = document.getString("name");
        String email = document.getString("email");
        String phoneNumber = document.getString("phoneNumber");
        ArrayList<String> joinList = (ArrayList<String>) document.get("joinList");
        return new Organizer(deviceID,
                name,
                email,
                phoneNumber,
                joinList);
    }
    /**
     * Private method to retrieve organizer class. Assumption is that Entrant already exists.
     * Fetches existing entrant in the database and turns it into a placeholder class
     * @param document The Document snapshot of the user ID query
     * @return The placeholder of organizer
     */
    private Entrant generateEntrant(DocumentSnapshot document, String deviceID) {
        String name = document.getString("name");
        String email = document.getString("email");
        String phoneNumber = document.getString("phoneNumber");
        ArrayList<String> joinList = (ArrayList<String>) document.get("joinList");
        return new Entrant(deviceID,
                name,
                email,
                phoneNumber,
                joinList);
    }


    /**
     * Removes the entrant from the database. What also needs to be deleted is the entrantID from
     * all events in which the entrant is entrolled in AND all waitlists the entrant is enroled in
     */
    public void removeEntrantUser( ) {

    }

    /**
     * Removes the Organizer from the database. What also needs to be deleted are:
     * Events
     * And on delete events delete users from waitlist and event (this can probably be handled in deleteEvent() in event controller
     */
    public void removeOrganizerUser() {

    }

    public interface OnUserLoaded {

        // any function that calls
        // must implement the following callback functions
        void onUserLoaded(User user);

    }
//    private Entrant getEntrant(DocumentSnap)
    public interface OnExistsUser {
        void onExistsUser(boolean userExists, User user);
    }
}


