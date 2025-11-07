package com.example.willowevents;
import android.net.Uri;
import androidx.annotation.Nullable;
import java.util.Date;

public interface CreateEventUseCase {
    final class Request {
        public String title;
        public String description;
        // Firestore will store these as Timestamp; model uses java.util.Date for ergonomics
        public String lotteryDetails;
        public Date eventDate;
        public Date registrationOpenDate;
        public Date registrationCloseDate;
        public @Nullable Integer capacity; // null => unlimited
        public @Nullable Integer waitlistLimit;
        public String organizerId; // required
        public @Nullable Uri posterUri; // reserved for later
        public boolean requireGeo; // reserved for later (default false)
    }


    final class Response { public String eventId; }


    interface Callback {
        void onSuccess(Response r);
        void onError(Throwable t);
    }


    void execute(Request req, Callback cb);
}
