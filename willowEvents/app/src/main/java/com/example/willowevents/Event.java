package com.example.willowevents;

import java.util.ArrayList;

public class Event {

    // TODO (probably for me Jeanard!): Find a way to uniquely identify an event ID (and should follow the same ID as the waitlist)
    // my idea is to have device ID +    and Organizer has a soft aggregation
    // since organizer can join events too I suggest moving to USER as abstract class and by inheritance split to organizer and user



    private String eventID; // Can be changed to int it needed
    private String name;
    private String details = " ";
    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";

    private String eventDate = "1/1/25|12:00PM";
    private String registrationDeadline = "1/1/25|12:00PM";
    private String registrationOpen ="1/1/25|12:00PM";

    private String location;

    // private Boolean location;   // figure something out



    private ArrayList<User> waitList;
    private ArrayList<User> cancelList;
    private ArrayList<User> aproveList;

    private int waitListLimit;



    public Event(String name){
        this.name = name;
        eventID = "Random non duplicate ID";
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDetails(String details) {this.details = details;}
    public void setLotteryDetails(String lotteryDetails){ this.lotteryDetails = lotteryDetails;}

    public void setEventDate(String eventDate) {this.eventDate = eventDate;}

    public void setRegistrationDeadline(String registrationDeadline) {this.registrationDeadline = registrationDeadline;}

    public void setRegistrationOpen(String registrationOpen) {this.registrationOpen = registrationOpen;}
    public void setWaitListLimit(int waitListLimit) { this.waitListLimit = waitListLimit;}
    public void setLocation(String location) {this.location = location; }

    public String getName() {
        return name;
    }

    public String getDetails(){ return details;}

    public String getLotteryDetails(){ return lotteryDetails;}

    public ArrayList<User> getWaitList(){ return waitList;}

    public ArrayList<User> getAproveList() { return aproveList;}

    public ArrayList<User> getCancelList() { return cancelList;}
    public int getWaitListLimit() { return waitListLimit;}
    public String getLocation() {return location;}

    public String getEventDate() {
        return eventDate;
    }

    public String getRegistrationDeadline() {
        return registrationDeadline;
    }
    public String getRegistrationOpen(){
        return registrationOpen;
    }

}
