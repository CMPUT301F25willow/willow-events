package com.example.willowevents.model;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

/**
 * This class defines the Notification object
 */
public class Notification {
    String eventId;
    String senderID;
    String recipientID;
    String eventName;
    String notificationMessage;
    LocalDateTime dateTime;

    /**
     * Instatiator method for firestore
     */
    public Notification() {

    }

    public Notification(Event event, String notificationText, String recipientID) {
        this.eventId = event.getId();
        this.eventName = event.getTitle();
        this.notificationMessage = notificationText;
        this.senderID = event.getOrganizerId();
        this.recipientID = recipientID;
        dateTime = now();
    }

    //Invite makes notification with event
    public Notification(Event event, String recipientID, boolean chosen){
        eventId = event.getId();
        eventName = event.getTitle();
        if (chosen) {
            notificationMessage = "Congratulations! You have been invited to the " + eventName + " event.";
        } else {
            notificationMessage = "Unfortunately, you were not selected to participate in the " + eventName + " event.";
        }
        dateTime = now();
        senderID = event.getOrganizerId();
        this.recipientID = recipientID;

        //TODO: add to database?
    }

    // getters
    public String getNotificationMessage(){
        return notificationMessage;
    }

    public String getEventName(){
        return eventName;
    }

    public String getEventId(){
        return eventId;
    }

    public LocalDateTime getDateTime(){
        return dateTime;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }
    
    public String getRecipientID() {
        return recipientID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }
}
