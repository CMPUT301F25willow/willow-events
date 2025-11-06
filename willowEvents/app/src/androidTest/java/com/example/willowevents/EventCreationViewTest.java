package com.example.willowevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;

import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.willowevents.R;
import com.example.willowevents.organizer.EventCreationView;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.contrib.PickerActions;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventCreationViewTest {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Before
    public void setUp() throws Exception {
        // Point SDKs to local emulators
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        db.useEmulator("10.0.2.2", 8080);
        auth.useEmulator("10.0.2.2", 9099);
        // Sign in anonymously for organizerId
        Tasks.await(auth.signInAnonymously());
    }

    @Test
    public void datePickersUpdate() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            // Event date
            onView(withId(R.id.event_start_date_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class))
                    .perform(PickerActions.setDate(2026, 1, 2)); // Jan 2, 2026
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class))
                    .perform(PickerActions.setTime(9, 30));
            onView(withText("OK")).perform(click());

            // Open date
            onView(withId(R.id.registration_open_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class))
                    .perform(PickerActions.setDate(2025, 12, 31));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class))
                    .perform(PickerActions.setTime(8, 0));
            onView(withText("OK")).perform(click());

            // Close date
            onView(withId(R.id.registration_deadline_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class))
                    .perform(PickerActions.setDate(2026, 1, 1));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class))
                    .perform(PickerActions.setTime(12, 0));
            onView(withText("OK")).perform(click());

            // Check text fields not equal to the default seed value
            onView(withId(R.id.event_start_date))
                    .check(matches(not(withText("1/1/25|12:00"))));
            onView(withId(R.id.registration_open_date))
                    .check(matches(not(withText("1/1/25|12:00"))));
            onView(withId(R.id.registration_deadline_date))
                    .check(matches(not(withText("1/1/25|12:00"))));
        }
    }

    @Test
    public void createEvent_happyPath_writesToFirestore() throws Exception {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            onView(withId(R.id.event_name_entry)).perform(typeText("Espresso Event"), closeSoftKeyboard());
            onView(withId(R.id.additional_details_entry)).perform(typeText("Espresso description"), closeSoftKeyboard());

            // Set dates in proper order: open < close < event
            onView(withId(R.id.registration_open_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 1));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.registration_deadline_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 2));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.event_start_date_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 3));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.create_event_create_button)).perform(click());

            // Verify Firestore emulator has a doc with this title
            var snap = Tasks.await(db.collection("events")
                    .whereEqualTo("title", "Espresso Event").get());
            org.junit.Assert.assertTrue(snap.size() >= 1);
        }
    }

    @Test
    public void waitlistLimit_digitsOnlyValidation() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            // Enable limit and type non-digits
            onView(withId(R.id.limit_waitlist_checkbox)).perform(click());
            onView(withId(R.id.waitlist_size_limit_entry)).perform(typeText("abc"), closeSoftKeyboard());

            // Minimal required fields to trigger create
            onView(withId(R.id.event_name_entry)).perform(typeText("Bad Limit"), closeSoftKeyboard());
            onView(withId(R.id.additional_details_entry)).perform(typeText("desc"), closeSoftKeyboard());

            // Set reasonable dates
            onView(withId(R.id.registration_open_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 1));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.registration_deadline_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 2));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.event_start_date_button)).perform(click());
            onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2026, 1, 3));
            onView(withText("OK")).perform(click());
            onView(isAssignableFrom(TimePicker.class)).perform(PickerActions.setTime(9, 0));
            onView(withText("OK")).perform(click());

            onView(withId(R.id.create_event_create_button)).perform(click());

            // Assert EditText shows error
            onView(withId(R.id.waitlist_size_limit_entry))
                    .check(matches(hasErrorText("Digits only")));
        }
    }
}

