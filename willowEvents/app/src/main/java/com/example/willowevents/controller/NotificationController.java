package com.example.willowevents.controller;

import android.util.Log;

import com.example.willowevents.model.Event;
import com.example.willowevents.model.Notification;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * This connects the database to the views
 */
public class NotificationController {
    private FirebaseFirestore notificationDB;

    // get all users
    private CollectionReference notificationsRef;
    public NotificationController() {

        // get pointer to database
        notificationDB = FirebaseFirestore.getInstance();
        // get collection of notifications
        notificationsRef = notificationDB.collection("notifications");
    }

    /**
     * This method is to be called after initializing NotificationController ONLY FOR testing
     * Calling this method will make the controller point to the `notifications_test` collection instead of the standard `notifications` collection
     */
    public void useTestDatabase() {
        notificationsRef = notificationDB.collection("notifications_test");
    }


    /**
     * Retrieves a notification based on the notificationID provided
     * @param notificationID
     */
    public void getNotification(String notificationID, OnNotificationGenerated callback) {

        DocumentReference docRef = notificationsRef.document(notificationID);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Notification notification = null;

            // IF NOTIFICATION EXISTS
            if (documentSnapshot.exists()) {
                // transform notification to class
                notification = documentSnapshot.toObject(Notification.class);
            }

            // Pass notification to callback function
            callback.onNotificationLoaded(notification);
        });
    }

    /**
     * Retrieves ALL EXISTING NOTIFICATIONS
     * @param callback is the callback function to pass all generated notifications
     */
    public void generateAllNotifications(OnNotificationsGenerated callback) {

        notificationsRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            else {
                // prepare empty notification array to populate
                ArrayList<Notification> notifications    = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot: value){

                    // transform notification to class
                    Notification notification = snapshot.toObject(Notification.class);

                    notifications.add(notification);
                }
                callback.onNotificationsLoaded(notifications);
            }
        });
    }

    /**
     * Generates ALL NOTIFICATIONS destined to a certain specified user
     * @param receiverID is the String of the user's ID who receives the notification
     */
    public void generateUserNotifications(String receiverID, OnNotificationsGenerated callback) {

        notificationsRef.addSnapshotListener((value, error) -> {

            if (error != null) {
                Log.e("Firestore", error.toString());
            }
            else {
                // prepare empty notification array to populate
                ArrayList<Notification> notifications    = new ArrayList<>();
                for (QueryDocumentSnapshot snapshot: value){

                    // transform notification to class
                    Notification notification = snapshot.toObject(Notification.class);

                    // condition is that the notification is destined TO THE SPECIFIED Receiver ID
                    if (notification.getRecipientID().equals(receiverID)) {
                        notifications.add(notification);
                    }
                }
                // PASS THE QUERIED NOTIFICATIONS
                callback.onNotificationsLoaded(notifications);
            }
        });
    }

    /**
     * Deletes a notification given a Notification in the database
     * @param notification MUST HAVE AN ID
     *
     */
    public void deleteNotification(Notification notification) {
        DocumentReference docRef = notificationsRef.document(notification.getID());
        docRef.delete();

    }

    /**
     * Adds a notification for the first time in the database
     * ASSUMPTION: MUST NOT BE AN EXISTING NOTIFICATION IN THE DATABASE as a new notification ID Will be generated
     * @param notification is the model / placeholder of the notification we want to store in the database
     */
    public void addNotification(Notification notification) {
        // GENERATE UNIQUE NOTIFICATION ID
        DocumentReference docRef = notificationsRef.document();

        // SET NOTIFICATION ID
        notification.setID(docRef.getId());

        // PUT NOTIFICATION IN
        docRef.set(notification);
    }


    /**
     * callback interface for when a notification is generated
     */
    public interface OnNotificationGenerated {
        void onNotificationLoaded(Notification notification);
    }

    /**
     * callback interface for when an array of notifications is generated from the database
     */
    public interface OnNotificationsGenerated {

        void onNotificationsLoaded(ArrayList<Notification> notifications);
    }



}
