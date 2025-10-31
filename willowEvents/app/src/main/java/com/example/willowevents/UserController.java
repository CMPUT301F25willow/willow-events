package com.example.willowevents;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


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

    public User getUser(String userID) {
        // get document via id
        DocumentReference docRef = usersRef.document("userID");

        https://firebase.google.com/docs/firestore/query-data/get-data?hl=fr#java
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userType = document.getString("userType");
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String phoneNumber = document.getString("phoneNumber");
                        ArrayList<String> joinList = (ArrayList<String>) document.get("joinList");

                        if (userType == "organizer") {
                            Organizer organizer = new Organizer();
                        }
                        else {
                            Entrant entrant = new Entrant(userID,
                                name,
                                email,
                                phoneNumber,
                                joinList);
                        }

                    } else {
                        Log.e("Firestore", "get failed with ", task.getException());
                    }
                }
            }
        });
        return null;
    }


    /**
     * Removes the entrant from the database. What also needs to be deleted is the entrantID from
     * all events in which the entrant is entrolled in AND all waitlists the entrant is enroled in
     */
    public void removeEntrantUser(){

    }

    /**
     * Removes the Organizer from the database. What also needs to be deleted are:
     * Events
     * And on delete events delete users from waitlist and event (this can probably be handled in deleteEvent() in event controller
     */
    public void removeOrganizerUser() {

    }

}
