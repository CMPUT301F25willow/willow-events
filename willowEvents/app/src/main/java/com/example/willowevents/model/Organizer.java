package com.example.willowevents.model;

import java.util.ArrayList;

/**
 * This is a class that defines an Organizer object which extends user
 */
public class Organizer extends User {

    private ArrayList<String> createdList;
    // List of events created by organizer

    /**
     * This creates a new Organizer Object given fields
     * @param ID            - String
     * @param name          - String
     * @param Email         - String
     * @param phoneNumber   - String
     * @param joinList      - ArrayList<String>
     */
    public Organizer(
            String ID,
            String name,
            String Email,
            String phoneNumber,
            ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, "organizer", joinList);
    }


    /**
     * This is a base constructor so that firebase can create a constructor
     */
    public Organizer() {
        super();
    }

    /**
     * This creates an organizer object
     * @param name  - String
     */
    Organizer(String name){
        super(name);
    }

    /**
     * This returns an ArrayList<String> createdList which contains IDs to events
     * @return createdList - contains event IDs
     */
    public ArrayList<String> getCreatedList() { return this.createdList; }



}
