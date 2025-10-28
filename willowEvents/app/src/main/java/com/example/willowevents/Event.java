package com.example.willowevents;

import java.util.ArrayList;

public class Event {
    private String name;
    private String details;
    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";
    // private Boolean location;   // figure soemthing out

    //private Organizer prganizer;

    // private Time deadline; soemthing

    private ArrayList<Entrant> waitlist;
    private ArrayList<Entrant> cancellist;
    private ArrayList<Entrant> aprovelist;



    public Event(String name){
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDetails(){ return details;};

    public String getLotteryDetails(){ return lotteryDetails;};

    public ArrayList<Entrant> getWaitlist(){ return waitlist;};

    public ArrayList<Entrant> getAprovelist() { return aprovelist;}

    public ArrayList<Entrant> getCancellist() { return cancellist;}
}
