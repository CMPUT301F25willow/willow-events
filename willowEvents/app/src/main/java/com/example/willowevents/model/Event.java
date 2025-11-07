package com.example.willowevents.model;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;

import org.checkerframework.common.returnsreceiver.qual.This;

import java.util.Date;
import java.util.List;

/**
 * Defines an event object, storing the data about an Event in a convient place to be read by
 * and passed between other classes
 */
public class Event {
    private String id; // Firestore doc id
    private String title;
    private String description;
    private Date eventDate;
    private Date registrationOpenDate;
    private Date registrationCloseDate;

    String lotteryDetails =
            "\n\nLottery System:" +
                    "\nAfter the deadline has ended a specified number of entrants will be randomly selected from the waiting list." +
                    "\nIf you have been invited to an event, you will be notified immediately."+
                    "\nIf you have not been invited, it is possible to be invited again if an entrant declines their invitation, " +
                    "in which case a new entrant will be randomly selected again from the waiting list.";

    // Optional capacity (not wired in UI yet, kept for future)
    private @Nullable Integer capacity; // null => unlimited

    // NEW: waitlist buckets (store userIds)
    private List<String> waitlist = new ArrayList<>();
    private List<String> cancelledList = new ArrayList<>();
    private List<String> approvedList = new ArrayList<>();
    private List<String> inviteList = new ArrayList<>();


    // Optional limit on waitlist size (from checkbox + number)
    private @Nullable Integer waitlistLimit; // null => no limit


    // Organizer info (required)
    private String organizerId;


    // Reserved for future
    private @Nullable String posterUrl;
    private boolean requireGeo;

    public Event(){}
    public Event(String name) {
        this.title = name;
    }

    /**
     * This creates a new Event object
     * @param title         - String
     * @param description   - String
     * @param lotteryDetails- String
     * @param eventDate     - Date
     * @param regOpen       - Date
     * @param regClose      - Date
     * @param capacity      - Integer
     * @param waitlistLimit - Integer
     * @param organizerId   - String
     * @param posterUrl     - String
     * @param requireGeo    - boolean
     * @return event
     */
    public static Event fromCreation(
            String title,
            String description,
            String lotteryDetails,
            Date eventDate,
            Date regOpen,
            Date regClose,
            @Nullable Integer capacity,
            @Nullable Integer waitlistLimit,
            String organizerId,
            @Nullable String posterUrl,
            boolean requireGeo
    ) {

        Event e = new Event();
        e.title = title;
        e.description = description + "\n\nLottery System:" +
                "\nAfter the deadline has ended a specified number of entrants will be randomly selected from the waiting list." +
                "\nIf you have been invited to an event, you will be notified immediately."+
                "\nIf you have not been invited, it is possible to be invited again if an entrant declines their invitation, " +
                "in which case a new entrant will be randomly selected again from the waiting list.";
        e.lotteryDetails = lotteryDetails;
        e.eventDate = eventDate;
        e.registrationOpenDate = regOpen;
        e.registrationCloseDate = regClose;
        e.capacity = capacity; //invite list limit
        e.waitlistLimit = waitlistLimit;
        e.organizerId = organizerId;
        e.posterUrl = posterUrl;
        e.requireGeo = requireGeo;
// lists start empty
        e.waitlist = new ArrayList<>();
        e.cancelledList = new ArrayList<>();
        e.approvedList = new ArrayList<>();
        e.inviteList = new ArrayList<>();
        return e;
    }


    // Getters (add setters if you need mutation)

    /**
     * This returns a String id
     * @return id
     */
    public String getId() { return id; }

    /**
     * This sets a String id
     * @param id - String id for event
     */
    public void setId(String id) { this.id = id; }

    /**
     * This returns a String title
     * @return title
     */
    public String getTitle() { return title; }

    /**
     * This returns a String description
     * @return description
     */
    public String getDescription() { return description; }

    /**
     * This returns a Date eventDate
     * @return eventDate
     */
    public Date getEventDate() { return eventDate; }

    /**
     * This returns a Date registrationOpenDate
     * @return registrationOpenDate
     */
    public Date getRegistrationOpenDate() { return registrationOpenDate; }

    /**
     * This returns a Date registrationCloseDate
     * @return registrationCloseDate
     */
    public Date getRegistrationCloseDate() { return registrationCloseDate; }

    /**
     * This returns an Integer capacity = Invitelistlimit
     * @return capacity
     */
    public @Nullable Integer getInvitelistlimit() { return capacity; }

    /**
     * This returns a String lotterydetails
     * @return lotteryDetails
     */
    public String getLotteryDetails() {
        return this.lotteryDetails;
    }

    /**
     * This returns a List<String> waitlist
     * @return waitlist
     */
    public List<String> getWaitlist() { return waitlist; }
    /**
     * This returns a List<String> cancelledList
     * @return cancelledList
     */
    public List<String> getCancelledList() { return cancelledList; }
    /**
     * This returns a List<String> approvedList
     * @return approvedList
     */
    public List<String> getApprovedList() { return approvedList; }
    /**
     * This returns a List<String> inviteList
     * @return inviteList
     */
    public List <String> getInviteList() {return inviteList; }

    /**
     * This returns an Integer waitlistLimit
     * @return waitlistLimit
     */
    public @Nullable Integer getWaitlistLimit() { return waitlistLimit; }

    /**
     * This returns a String organizerId
     * @return organizerId
     */
    public String getOrganizerId() { return organizerId; }

    /**
     * This returns a String posterUrl
     * @return posterUrl
     */
    public @Nullable String getPosterUrl() { return posterUrl; }

    /**
     * This returns a Boolean requireGeo
     * @return requireGeo
     */
    public boolean isRequireGeo() { return requireGeo; }

    /**
     * This sets a String title
     * @param title - String title for event
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * This sets a String description
     * @param description - String description for event
     */
    public void setDescription(String description) {
        this.description = description;
    }
    /**
        * This sets a String organizerId
     * @param organizerId - String organizerId for event
     */
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    /**
     * This sets a String eventDate
     * @param eventDate - String eventDate for event
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
    /**
     * This sets a String registrationOpenDate
     * @param registrationOpenDate - String registrationOpenDate for event
     */
    public void setRegistrationOpenDate(Date registrationOpenDate) {
        this.registrationOpenDate = registrationOpenDate;
    }
    /**
     * This sets a String registrationOpenDate
     * @param registrationCloseDate - String registrationCloseDate for event
     */
    public void setRegistrationCloseDate(Date registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }
    /**
     * This sets a String waitlist
     * @param waitlist - String waitlist for event
     */
    public void setWaitlist(List<String> waitlist) {
        this.waitlist = waitlist;
    }
    /**
     * This sets a String cancelledList
     * @param cancelledList - String cancelledList for event
     */
    public void setCancelledList(List<String> cancelledList) {
        this.cancelledList = cancelledList;
    }
    /**
     * This sets a String approvedList
     * @param approvedList - String approvedList for event
     */
    public void setApprovedList(List<String> approvedList) {
        this.approvedList = approvedList;
    }
    /**
     * This sets a String capacity
     * @param capacity - String capacity for event
     */
    public void setInvitelistlimit(Integer capacity) {this.capacity = capacity;
    }
    public void setRequireGeo(boolean requireGeo) {
         this.requireGeo = requireGeo;
    }
}
