package com.example.willowevents;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;


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
     * Adds a user to the database given an entrnat. Gives it a unique tag "Entrant" in the databse
     */
    public void addEntrantUser() {

    }

    /**
     * Adds the
     */
    public void addOrganizerUser() {
    }

    /**
     * Given a userID in the form of a String, return the user details from the database
     * User can only be retrieved via a callback function
     * @param userID is the ID of the user
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
                if (userType == "organizer") {
//                    user = new Organizer(userID,
//                            name,
//                            email,
//                            phoneNumber,
//                            joinList);
                    user = new Entrant(userID,
                            name,
                            email,
                            phoneNumber,
                            joinList);
                } else {
                    user = new Entrant(userID,
                            name,
                            email,
                            phoneNumber,
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

}


