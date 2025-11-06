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


    private Date eventDate;
    private Date registrationOpenDate;
    private Date registrationCloseDate;


    // Optional capacity (not wired in UI yet, kept for future)
    private @Nullable Integer capacity; // null => unlimited


    // NEW: waitlist buckets (store userIds)
    private List<String> waitlist = new ArrayList<>();
    private List<String> cancelledList = new ArrayList<>();
    private List<String> approvedList = new ArrayList<>();


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
        e.eventDate = eventDate;
        e.registrationOpenDate = regOpen;
        e.registrationCloseDate = regClose;
        e.capacity = capacity;
        e.waitlistLimit = waitlistLimit;
        e.organizerId = organizerId;
        e.posterUrl = posterUrl;
        e.requireGeo = requireGeo;
// lists start empty
        e.waitlist = new ArrayList<>();
        e.cancelledList = new ArrayList<>();
        e.approvedList = new ArrayList<>();
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


    public List<String> getWaitlist() { return waitlist; }
    public List<String> getCancelledList() { return cancelledList; }
    public List<String> getApprovedList() { return approvedList; }


    public @Nullable Integer getWaitlistLimit() { return waitlistLimit; }


    public String getOrganizerId() { return organizerId; }


    public @Nullable String getPosterUrl() { return posterUrl; }
    public boolean isRequireGeo() { return requireGeo; }
}
