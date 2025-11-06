package com.example.willowevents.model;

import java.util.ArrayList;
import java.util.Date;

public class Event {

    // TODO (probably for me Jeanard!): Find a way to uniquely identify an event ID (and should follow the same ID as the waitlist)
    // my idea is to have device ID +    and Organizer has a soft aggregation
    // since organizer can join events too I suggest moving to USER as abstract class and by inheritance split to organizer and user

    private String name;        //title
    private String details;     //details/long text
    // Question: How does a user choose to be an organizer? From the login screen or is a specific email is required?
    // - Michelle

    private String lotteryDetails = "This is lottery details which is seen in a scrollable form";
    private String id;          //combine this with firestore doc id
    private String bannerURL;    //banner

    private Integer waitlistlimit;

    private Date eventDate;
    private Date registrationDeadline;
    private Date registrationOpen;

    // private Boolean location;   // figure something out

    //---------------Participant lists--------------------------
    private ArrayList<Entrant> waitlist;        //opted in but didn't make it. Possible to join again if participant cancels
    private ArrayList<Entrant> cancellist;      //cancelled or removed
    private ArrayList<Entrant> approvelist;      //won and accepted?
    private ArrayList<Entrant> invitlist;    // selcted to join event, waiting for approval

    //----------Lottery Stuff---------------------------------
    private Boolean lotteryDone;                //has the lottery been done?

    //------------FireStore--------------------
    public Event() {
    }

    ;

    //-----------------------------------------------------------
    public Event(String name, String details, String lotteryDetails, String id, String bannerURL, Date registrationOpen, Date registrationDeadline, Date eventDate) {
        this.name = name;
        this.details = details;
        this.lotteryDetails = lotteryDetails;
        this.id = id;
        this.bannerURL = bannerURL;
        this.registrationOpen = registrationOpen;
        this.registrationDeadline = registrationDeadline;
        this.eventDate = eventDate;
        this.waitlistlimit = null;
        //eventID = "Random non duplicate ID"; <-I dont think we need this as firebase creates its own id, stored above as id - adrian


        this.waitlist = new ArrayList<>();
        this.approvelist = new ArrayList<>();
        this.cancellist = new ArrayList<>();
        this.invitlist = new ArrayList<>();
    }

    public Event(String eventTitle) {
        this.name = eventTitle;
    }

    //---------------------Getters----------------------------
    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    public String getDetails() {
        return details;
    }

    ;

    public String getLotteryDetails() {
        return lotteryDetails;
    }

    ;

    public ArrayList<Entrant> getWaitlist() {
        return waitlist;
    }


    ;

    public ArrayList<Entrant> getAprovelist() {
        return approvelist;
    }

    public ArrayList<Entrant> getCancellist() {
        return cancellist;
    }
    public ArrayList<Entrant> getInvitelist() {return invitlist;}

    public Date getEventDate() {
        return eventDate;
    }

    public Date getRegistrationDeadline() {
        return registrationDeadline;
    }

    public Date getRegistrationOpen() {
        return registrationOpen;
    }

    public String getBannerURL(){ return bannerURL;}


    //--------------Setters--------------------------------
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setLotteryDetails(String lotteryDetails) {
        this.lotteryDetails = lotteryDetails;
    }

    public void setWaitlist(ArrayList<Entrant> waitlist) {
        this.waitlist = waitlist;
    }

    public void setAprovelist(ArrayList<Entrant> approvelist) {
        this.approvelist = approvelist;
    }

    public void setCancellist(ArrayList<Entrant> cancellist) {
        this.cancellist = cancellist;
    }
    public void setInvitelist(ArrayList<Entrant> invitlist) { this.invitlist = invitlist;}

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setRegistrationDeadline(Date registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public void setRegistrationOpen(Date registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public void setBannerURL(String bannerURL) {
        this.bannerURL = bannerURL;
    }

    public void setWaitlistLimit(Integer waitlistLimit) {
    }
}