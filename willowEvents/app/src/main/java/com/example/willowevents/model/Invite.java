package com.example.willowevents.model;

import com.example.willowevents.model.Event;

public class Invite {
    Event event;
    Entrant entrant;
    String status;
    //Valid statuses are INVITED ACCEPTED and DECLINED
    public Invite(Event event, Entrant entrant, String stat){
        this.event = event;
        this.entrant = entrant;
        this.status = stat;
    }
}