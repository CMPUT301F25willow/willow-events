package com.example.willowevents;

import java.util.ArrayList;

public class Event {

    // TODO (probably for me Jeanard!): Find a way to uniquely identify an event ID (and should follow the same ID as the waitlist)
    // my idea is to have device ID +    and Organizer has a soft aggregation
    // since organizer can join events too I suggest moving to USER as abstract class and by inheritance split to organizer and user

    // Question: How does a user choose to be an organizer? From the login screen or is a specific email is required?
    // - Michelle

    private String eventID; // Can be changed to int it needed
    private String name;
    private String details; // description for event
    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";

    // private Boolean location;   // figure something out

    // private Time deadline; soemthing

    private ArrayList<User> waitList;
    private ArrayList<User> cancelList;
    private ArrayList<User> aproveList;



    public Event(String name){
        this.name = name;
        eventID = "Random non duplicate ID";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {this.details = details;}
    public void setLotteryDetails(String lotteryDetails){ this.lotteryDetails = lotteryDetails;}


    public String getName() {
        return name;
    }

    public String getDetails(){ return details;}

    public String getLotteryDetails(){ return lotteryDetails;}

    public ArrayList<User> getWaitList(){ return waitList;}

    public ArrayList<User> getAproveList() { return aproveList;}

    public ArrayList<User> getCancelList() { return cancelList;}
}
