package com.example.willowevents;

import androidx.annotation.Nullable;
import com.example.willowevents.model.Event;
import java.util.HashMap;
import java.util.Map;


final class EventMapper {
    private EventMapper() {}


    static Map<String, Object> toMap(Event e, String eventId) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", eventId);
        m.put("title", e.getTitle());
        m.put("description", e.getDescription());
        m.put("eventDate", e.getEventDate()); // stored as Timestamp by SDK
        m.put("registrationOpenDate", e.getRegistrationOpenDate());
        m.put("registrationCloseDate", e.getRegistrationCloseDate());
        if (e.getInvitelistlimit() != null) m.put("capacity", e.getInvitelistlimit());
        if (e.getWaitlistLimit() != null) m.put("waitlistLimit", e.getWaitlistLimit());
        m.put("waitlist", e.getWaitlist()); // [] initially
        m.put("cancelledList", e.getCancelledList()); // [] initially
        m.put("approvedList", e.getApprovedList()); // [] initially
        m.put("organizerId", e.getOrganizerId());
// reserved
        if (e.getPosterUrl() != null) m.put("posterUrl", e.getPosterUrl());
        m.put("requireGeo", e.isRequireGeo());
        return m;
    }
}
