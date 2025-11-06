package com.example.willowevents.model;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.Nullable;
import java.util.Date;
import java.util.List;


public class Event {
    private String id; // Firestore doc id
    private String title;
    private String description;

    private String lotteryDetails =
            "\n\nLottery System:" +
                    "\nAfter the deadline has ended a specified number of entrants will be randomly selected from the waiting list." +
                    "\nIf you have been invited to an event, you will be notified immediately."+
                    "\nIf you have not been invited, it is possible to be invited again if an entrant declines their invitation, " +
                    "in which case a new entrant will be randomly selected again from the waiting list.";

    private Date eventDate;
    private Date registrationOpenDate;
    private Date registrationCloseDate;


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
        e.description = description;
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
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }


    public Date getEventDate() { return eventDate; }
    public Date getRegistrationOpenDate() { return registrationOpenDate; }
    public Date getRegistrationCloseDate() { return registrationCloseDate; }


    public @Nullable Integer getCapacity() { return capacity; }

    public void setLotteryDetails(String lotteryDetails) {
        this.lotteryDetails = lotteryDetails;
    }

    public List<String> getWaitlist() { return waitlist; }
    public List<String> getCancelledList() { return cancelledList; }
    public List<String> getApprovedList() { return approvedList; }


    public @Nullable Integer getWaitlistLimit() { return waitlistLimit; }


    public String getOrganizerId() { return organizerId; }


    public @Nullable String getPosterUrl() { return posterUrl; }
    public boolean isRequireGeo() { return requireGeo; }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setRegistrationOpenDate(Date registrationOpenDate) {
        this.registrationOpenDate = registrationOpenDate;
    }

    public void setRegistrationCloseDate(Date registrationCloseDate) {
        this.registrationCloseDate = registrationCloseDate;
    }

    public void setWaitlist(List<String> waitlist) {
        this.waitlist = waitlist;
    }

    public void setCancelledList(List<String> cancelledList) {
        this.cancelledList = cancelledList;
    }

    public void setApprovedList(List<String> approvedList) {
        this.approvedList = approvedList;
    }

    public void setInvitelistlimit(int value) {
    }
}
