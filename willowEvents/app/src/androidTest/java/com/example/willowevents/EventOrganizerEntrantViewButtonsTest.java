package com.example.willowevents;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.Matchers.allOf;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.willowevents.organizer.EventOrganizerEntrantView;
import com.example.willowevents.organizer.UserListView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventOrganizerEntrantViewButtonsTest {

    @Rule
    public IntentsRule intentsRule = new IntentsRule(); // auto init/release Intents

    @Test
    public void clickingEnrolled_opensUserListWithExtras() {
        String eventId = "test-event-123";

        Intent launch = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventOrganizerEntrantView.class
        );
        launch.putExtra("Event ID", eventId);

        try (ActivityScenario<EventOrganizerEntrantView> sc =
                     ActivityScenario.launch(launch)) {

            onView(withId(R.id.enrolled_see_entrants_button)).perform(click());

            intended(allOf(
                    hasComponent(UserListView.class.getName()),
                    hasExtra("Type", "enrolled"),
                    hasExtra("Event ID", eventId)
            ));
        }
    }

    @Test
    public void clickingWaitlist_opensUserListWithExtras() {
        String eventId = "test-event-123";
        Intent launch = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventOrganizerEntrantView.class
        );
        launch.putExtra("Event ID", eventId);

        try (ActivityScenario<EventOrganizerEntrantView> sc =
                     ActivityScenario.launch(launch)) {
            onView(withId(R.id.waitlist_see_entrants_button)).perform(click());
            intended(allOf(
                    hasComponent(UserListView.class.getName()),
                    hasExtra("Type", "waitlist"),
                    hasExtra("Event ID", eventId)
            ));
        }
    }

    // Do the same pattern for invited/cancelled if you like.
}
