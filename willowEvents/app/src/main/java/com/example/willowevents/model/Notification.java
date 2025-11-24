package com.example.willowevents.model;


/**
 * Represents a class of notifications
 */
public class Notification {



    // sender ID:
    String senderID;

    // recipient ID:
    String recipientID;

    // Event ID:
    String eventID;

    // Status
    // TRUE: if notification is active /
    boolean status;

    /**
     * Instatiator method for firestore
     */
    public Notification() {

    }

    /**
     * Initiator class for testing
     * @param senderID is the STRING of the device ID of the sender
     * @param recipientID is the STRING of the device ID of the recipient
     * @param eventID is the STRING of the device ID of the event ID
     * @param status is TRUE if user wants notifications. FALSE otherwise.
     */
    public Notification(String senderID, String recipientID, String eventID, boolean status) {
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.eventID = eventID;
        this.status = status;
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

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
