package com.example.willowevents;

import com.example.willowevents.model.Event;
import com.example.willowevents.EventRepository;
import com.example.willowevents.MediaRepository;


public final class CreateEventUseCaseImpl implements CreateEventUseCase {
    private final EventRepository events;
    private final MediaRepository media; // poster reserved for later


    public CreateEventUseCaseImpl(EventRepository events, MediaRepository media) {
        this.events = events;
        this.media = media;
    }


    @Override
    public void execute(Request r, Callback cb) {
        try {
            validate(r);
            Event e = Event.fromCreation(
                    r.title, r.description, r.lotteryDetails,
                    r.eventDate, r.registrationOpenDate, r.registrationCloseDate,
                    r.capacity, r.waitlistLimit, r.organizerId,
                    /*posterUrl*/ null, /*requireGeo*/ r.requireGeo
            );


// Poster deferred: if r.posterUri == null, just create event
            if (r.posterUri == null) {
                events.createEvent(e, new EventRepository.StringCallback() {
                    @Override public void onSuccess(String eventId) {
                        Response resp = new Response(); resp.eventId = eventId; cb.onSuccess(resp);
                    }
                    @Override public void onError(Throwable t) { cb.onError(t); }
                });
            } else {
// Reserve upload path for later
                events.createEvent(e, new EventRepository.StringCallback() {
                    @Override public void onSuccess(String eventId) {
                        media.uploadPoster(r.posterUri, eventId, new MediaRepository.StringCallback() {
                            @Override public void onSuccess(String url) {
                                events.attachPoster(eventId, url, new EventRepository.VoidCallback() {
                                    @Override public void onSuccess() {
                                        Response resp = new Response(); resp.eventId = eventId; cb.onSuccess(resp);
                                    }
                                    @Override public void onError(Throwable t) { cb.onError(t); }
                                });
                            }
                            @Override public void onError(Throwable t) { cb.onError(t); }
                        });
                    }
                    @Override public void onError(Throwable t) { cb.onError(t); }
                });
            }
        } catch (Throwable t) {
            cb.onError(t);
        }
    }


    private void validate(Request r) {
        if (isBlank(r.title)) throw new IllegalArgumentException("Title is required");
        if (isBlank(r.description)) throw new IllegalArgumentException("Description is required");
        if (r.organizerId == null || r.organizerId.isEmpty()) throw new IllegalArgumentException("Organizer ID missing");
        if (r.eventDate == null) throw new IllegalArgumentException("Event date is required");
        if (r.registrationOpenDate == null) throw new IllegalArgumentException("Registration open date is required");
        if (r.registrationCloseDate == null) throw new IllegalArgumentException("Registration close date is required");
// Time ordering: regOpen <= regClose <= eventDate
        if (r.registrationOpenDate.after(r.registrationCloseDate))
            throw new IllegalArgumentException("Registration close must be after open");
        if (r.registrationCloseDate.after(r.eventDate))
            throw new IllegalArgumentException("Registration must close before event date");
// Capacity optional: if provided, must be > 0
        if (r.capacity != null && r.capacity <= 0)
            throw new IllegalArgumentException("Capacity must be greater than 0");
    }
    private boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }
}
