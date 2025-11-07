package com.example.willowevents.model;

import com.example.willowevents.model.Event;

/**
 * Defines an Invite object use to store information about what events users where invited to and
 * whether they have accepted or declined
 */
public class Invite {
    Event event;
    Entrant entrant;
    String status;
    //Valid statuses are INVITED ACCEPTED NOT INVITED and DECLINED
    public Invite(Event event, Entrant entrant, String stat){
        this.event = event;
        this.entrant = entrant;
        this.status = stat;
    }

    // Getters
    public Event getEvent() {
        return this.event;
    }

    public String getStatus() {
        return this.status;
    }

    public Entrant getUser() {
        return this.entrant;
    }

    // Setters
    public void setStatus(String stat) {
        this.status = stat;
    }
}