package com.example.willowevents;

public class Event {
    private String name;
    private String details;
    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";
    private Boolean location;   // figure soemthing out

    // private Time deadline; soemthing

   // private WaitList waitlist;
    // private CancelList cancellist;
    // private AproveList aprovelist;



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

    public String getLottery(){ return lotteryDetails;};
    

}
