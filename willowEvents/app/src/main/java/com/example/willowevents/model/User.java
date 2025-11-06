package com.example.willowevents.model;

import java.util.ArrayList;

public abstract class User {

    private String name;
    private String userID;
    private String email;
    private String phoneNumber;

    // MUST be a string because we must query from live DB model
    private ArrayList<String> joinList;
    // private ArrayList<Notifications> notificationsList;
    // implement based on how notification will be implemented

    // Names can be the same but IDs should be unique to each User

    public User() {}
    public User(
            String ID,
            String name,
            String Email,
            String phoneNumber,
            ArrayList<String> joinList
    ) {
        this.userID = ID;
        this.name = name;
        this.email = Email;
        this.phoneNumber = phoneNumber;
//        this.joinList = joinList;
    }

    public User(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.userID = ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<String> getJoinList() {
        return joinList;
    }

}
