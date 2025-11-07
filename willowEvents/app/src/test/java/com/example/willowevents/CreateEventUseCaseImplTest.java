package com.example.willowevents;

import com.example.willowevents.EventRepository;
import com.example.willowevents.MediaRepository;
import com.example.willowevents.model.Event;
import org.junit.Before;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class CreateEventUseCaseImplTest {

    private FakeEvents repo;
    private FakeMedia media;
    private CreateEventUseCase useCase;

    @Before
    public void setUp() {
        repo = new FakeEvents();
        media = new FakeMedia();
        useCase = new CreateEventUseCaseImpl(repo, media);
    }

    @Test
    public void happyPath_noLimits() {
        CreateEventUseCase.Request r = baseRequest();
        r.waitlistLimit = null;
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) {
                assertEquals("fake-id-1", resp.eventId);
                assertTrue(repo.created);
            }
            @Override public void onError(Throwable t) { fail("Unexpected error: " + t); }
        });
    }

    @Test
    public void error_missingTitle() {
        CreateEventUseCase.Request r = baseRequest();
        r.title = "   ";
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) { fail("Should fail"); }
            @Override public void onError(Throwable t) { assertTrue(t.getMessage().toLowerCase().contains("title")); }
        });
    }

    @Test
    public void error_regCloseAfterEvent() {
        CreateEventUseCase.Request r = baseRequest();
        // violate: regClose > eventDate
        Calendar c = Calendar.getInstance();
        c.setTime(r.eventDate);
        c.add(Calendar.DAY_OF_MONTH, 1);
        r.registrationCloseDate = c.getTime();
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) { fail("Should fail"); }
            @Override public void onError(Throwable t) { assertTrue(t.getMessage().toLowerCase().contains("before event")); }
        });
    }

    @Test
    public void error_waitlistLimitNonPositive() {
        CreateEventUseCase.Request r = baseRequest();
        r.waitlistLimit = 0;
        useCase.execute(r, new CreateEventUseCase.Callback() {
            @Override public void onSuccess(CreateEventUseCase.Response resp) { fail("Should fail"); }
            @Override public void onError(Throwable t) { assertTrue(t.getMessage().toLowerCase().contains("waitlist")); }
        });
    }

    private CreateEventUseCase.Request baseRequest() {
        Calendar now = Calendar.getInstance();
        Date regOpen = addDays(now.getTime(), 0);
        Date regClose = addDays(now.getTime(), 1);
        Date eventDate = addDays(now.getTime(), 2);

        CreateEventUseCase.Request r = new CreateEventUseCase.Request();
        r.title = "Test";
        r.description = "Desc";
        r.registrationOpenDate = regOpen;
        r.registrationCloseDate = regClose;
        r.eventDate = eventDate;
        r.organizerId = "uid123";
        r.capacity = null;
        r.waitlistLimit = null;
        r.posterUri = null;
        r.requireGeo = false;
        return r;
    }

    private static Date addDays(Date d, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DAY_OF_MONTH, days);
        return c.getTime();
    }

    // ---- Fakes ----
    static class FakeEvents implements EventRepository {
        boolean created = false;
        @Override public void createEvent(Event event, StringCallback cb) {
            created = true;
            cb.onSuccess("fake-id-1");
        }
        @Override public void attachPoster(String eventId, String posterUrl, VoidCallback cb) {
            cb.onSuccess();
        }
    }
    static class FakeMedia implements MediaRepository {
        @Override public void uploadPoster(android.net.Uri localUri, String eventId, StringCallback cb) {
            cb.onSuccess("https://example.com/poster.png");
        }
    }
}
