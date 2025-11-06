package com.example.willowevents;

import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Organizer;
import com.example.willowevents.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyStore;
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
    public void addEntrantUser(String userID) {
        Entrant user = new Entrant(userID,
                "No name",
                "No email",
                "No phone number",
                "entrant",
                new ArrayList<>());

        DocumentReference docRef = usersRef.document(userID);
        docRef.set(user);
    }

    /**
     * Adds a user to the database given an organizer. Gives it a unique tag "Entrant" in the database
     * @param userID is the Device-ID as a STRING of the user
     */
    public void addOrganizerUser(String userID) {
        Organizer user = new Organizer(userID,
                "No name",
                "No email",
                "No phone number",
                "organizer",
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
     * @param userID is the Device-ID as a STRING of the user
     *
     */
    public void getUser(String userID , OnUserLoaded callback) {
        // get document via id
        DocumentReference docRef = usersRef.document(userID);
        // https://firebase.google.com/docs/firestore/query-data/get-data?hl=fr#java
        docRef.get().addOnSuccessListener(snapshot -> {

            User user;

            // if user exists
            if (snapshot.exists()) {
                String userType = snapshot.getString("userType");
                String name = snapshot.getString("name");
                String email = snapshot.getString("email");
                String phoneNumber = snapshot.getString("phoneNumber");
                ArrayList<String> joinList = (ArrayList<String>) snapshot.get("joinList");
                if (Objects.equals(userType, "organizer")) {
                    user = new Organizer(userID,
                            name,
                            email,
                            phoneNumber,
                            userType,
                            joinList);
                } else {
                    user = new Entrant(userID,
                            name,
                            email,
                            phoneNumber,
                            userType,
                            joinList);
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
                String name = document.getString("name");
                String email = document.getString("email");
                String phoneNumber = document.getString("phoneNumber");
                ArrayList<String> joinList = (ArrayList<String>) document.get("joinList");
                if (Objects.equals(userType, "organizer")) {
                    user = new Organizer(deviceID,
                            name,
                            email,
                            phoneNumber,
                            userType,
                            joinList);
                } else {
                    user = new Entrant(deviceID,
                            name,
                            email,
                            phoneNumber,
                            userType,
                            joinList);
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

        // any function that calls getUser must implement the following callback functions
        void onUserLoaded(User user);

    }

    public interface OnExistsUser {
        void onExistsUser(boolean userExists, User user);
    }

}


