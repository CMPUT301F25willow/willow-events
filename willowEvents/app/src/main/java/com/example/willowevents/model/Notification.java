package com.example.willowevents.model;

import static java.time.LocalDateTime.now;

import java.time.LocalDateTime;

public class Notification {
    String eventId;
    String eventName;
    String listType;
    String notificationMessage;
    LocalDateTime dateTime;

    public Notification(Event event, String notificationText, String listType) {
        this.eventId = event.getId();
        this.eventName = event.getTitle();
        this.notificationMessage = notificationText;
        this.listType = listType;
        dateTime = now();
    }
    public Notification(Event event){
        eventId = event.getId();
        eventName = event.getTitle();
        listType = "invitedList";
        notificationMessage = "Congratulations! You have been invited to the "+eventName+" event";
        dateTime = now();
    }

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
}
