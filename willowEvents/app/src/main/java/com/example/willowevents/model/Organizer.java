package com.example.willowevents.model;

import java.util.ArrayList;

public class Organizer extends User {

    private ArrayList<String> createdList;
    // List of events created by organizer
    public Organizer(
            String ID,
            String name,
            String Email,
            String phoneNumber,
            ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, "organizer", joinList);
    };
    Organizer(String name){
        super(name);
    }

    public ArrayList<String> getCreatedList() { return this.createdList; };



}
