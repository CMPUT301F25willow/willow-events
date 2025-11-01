package com.example.willowevents;

import android.provider.CalendarContract;

import java.util.ArrayList;

public class Organizer extends User {

    private ArrayList<String> createdList;
    // List of events created by organizer
    public Organizer(String ID, String name,
String Email,
String phoneNumber,
ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, joinList);
    };

    public ArrayList<String> getCreatedList() { return this.createdList; };



}
