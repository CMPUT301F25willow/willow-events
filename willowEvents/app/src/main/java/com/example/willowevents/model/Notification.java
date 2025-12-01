package com.example.willowevents.model;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * This class defines the Notification object
 */
public class Notification {

    String ID;

    String eventId;
    String senderID;
    String recipientID;
    String eventName;
    String notificationMessage;
    Date dateTime;
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


<<<<<<< HEAD
=======
    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
>>>>>>> 09b8d8205d1aa1098c93cd33691d961ae0d9b46f

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
        dateTime = new Date();
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
        dateTime = new Date();
        senderID = event.getOrganizerId();
        this.recipientID = recipientID;

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

    public Date getDateTime(){
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
