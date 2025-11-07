package com.example.willowevents.model;

import java.util.ArrayList;

public class Organizer extends User {

    private ArrayList<Event> createdList;
    // List of events created by organizer
    public Organizer(
            String ID,
            String name,
            String Email,
            String phoneNumber,
            String userType,
            ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, userType, joinList);
    };
    Organizer(String name){
        super(name);
    }

    public ArrayList<Event> getCreatedList() { return this.createdList; };



}
