package com.example.willowevents;

import java.util.ArrayList;

public abstract class User {

    private String name;
    private String userID;
    private String email;
    private String phoneNumber;

    private ArrayList<Event> joinList;
    // private ArrayList<Notifications> notificationsList;
    // implement based on how notification will be implemented

    // Names can be the same but IDs should be unique to each User

    public User() {  }

    public void setID(String ID) {this.userID = ID;}

    public void setEmail(String email) {this.email = email;}

    public void setName(String name) {this.name = name;}

    public void setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}

    public String getName(){ return name; }

    public String getID() { return userID;}

    public String getEmail() { return email;}

    public String getPhoneNumber() { return phoneNumber;}

    public ArrayList<Event> getJoinList() { return joinList;}


}
