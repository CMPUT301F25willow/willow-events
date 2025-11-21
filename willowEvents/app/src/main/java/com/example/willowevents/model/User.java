package com.example.willowevents.model;

import java.util.ArrayList;

/**
 * This is an abstract class that defines a User. Extended by Entrant and Organizer (and eventually
 * Admin) this gives a framework for what data about a use rof the application should be stored
 */
public abstract class User {

    private String name;
    private String userID;
    private String email;
    private String phoneNumber;

    private String userType;

    // MUST be a string because we must query from live DB model

    private ArrayList<String> joinList;
    // private ArrayList<Notifications> notificationsList;
    // implement based on how notification will be implemented

    // Names can be the same but IDs should be unique to each User

    /**
     * This creates a User object
     */
    public User() {}

    /**
     * This creates a new object User
     * @param ID            - String
     * @param name          - String
     * @param Email         - String
     * @param phoneNumber   - String
     * @param userType      - String
     * @param joinList      - ArrayList<String>
     */
    public User(
            String ID,
            String name,
            String Email,
            String phoneNumber,
            String userType,
            ArrayList<String> joinList
    ) {
        this.userID = ID;
        this.name = name;
        this.email = Email;
        this.phoneNumber = phoneNumber;
        this.userType=userType;
//        this.joinList = joinList;
    }

    /**
     * This creates a User object with String name as parameter
     * @param name  - String
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * This sets String userID
     * @param ID    - String
     */
    public void setID(String ID) {
        this.userID = ID;
    }
    /**
     * This sets String email
     * @param email    - String
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * This sets String name
     * @param name    - String
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * This sets String phoneNumber
     * @param phoneNumber    - String
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * This returns String name
     * @return  name
     */
    public String getName() {
        return name;
    }

    /**
     * This returns String userID
     * @return userID
     */
    public String getID() {
        return userID;
    }

    /**
     * This returns String email
     * @return  email
     */
    public String getEmail() {
        return email;
    }

    /**
     * This returns String phoneNumber
     * @return  phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * This returns ArrayList<String> joinList
     * @return  joinList
     */
    public ArrayList<String> getJoinList() {
        return joinList;
    }

    /**
     * This returns String userType
     * @return  userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * This sets String userType
     * @param userType  - String
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }
}
