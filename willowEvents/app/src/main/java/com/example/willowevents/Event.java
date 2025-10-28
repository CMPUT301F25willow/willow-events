package com.example.willowevents;

public class Event {
    private String name;
    private String details;
    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";
    private Boolean location;   // figure soemthing out

    //private Organizer prganizer;

    // private Time deadline; soemthing

    private List waitlist;
    private List cancellist;
    private List aprovelist;



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

    public List getWaitlist(){ return waitlist;};

    public List getAprovelist() { return aprovelist;}

    public List getCancellist() { return cancellist;}
}
