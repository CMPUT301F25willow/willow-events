package com.example.willowevents;

import com.example.willowevents.model.Event;


public interface EventRepository {
    interface StringCallback { void onSuccess(String value); void onError(Throwable t); }
    interface VoidCallback { void onSuccess(); void onError(Throwable t); }


    void createEvent(Event event, StringCallback cb); // auto-generates eventId
    void attachPoster(String eventId, String posterUrl, VoidCallback cb); // reserved for later
}