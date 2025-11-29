package com.example.willowevents.controller;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This connects the database to the views
 */
public class NotificationController {
    private FirebaseFirestore notificationDB;

    // get all users
    private CollectionReference notificationRef;
    public NotificationController() {
        // get pointer to database
        notificationDB = FirebaseFirestore.getInstance();
        // get all users
        notificationRef = notificationDB.collection("notifications");
    }

    /**
     * This method is to be called after initializing NotificationController ONLY FOR testing
     * Calling this method will make the controller point to the `notifications_test` collection instead of the standard `notifications` collection
     */
    public void useTestDatabase() {
        notificationRef = notificationDB.collection("notifications_test");
    }


}
