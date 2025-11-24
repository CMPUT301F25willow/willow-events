package com.example.willowevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

import android.net.Uri;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.willowevents.organizer.EventCreationView;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Instrumentation tests that cover:
 * - Firestore integration for events
 * - UI behavior of EventCreationView (Espresso)
 * - Edge cases for waitlist limit, image selection, and basic validation
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventCreationViewTest {


    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TEST_COLL = "events_test";
    private final List<DocumentReference> createdDocs = new ArrayList<>();

    @Before
    public void signIn() throws Exception {
        // Requires Anonymous sign-in enabled in Firebase console
        Tasks.await(auth.signInAnonymously());
        assertNotNull("Auth user must be non-null", auth.getCurrentUser());
    }

    @After
    public void cleanUp() {
        // Best-effort cleanup of created docs
        for (DocumentReference ref : createdDocs) {
            ref.delete();
        }
    }

    @Test
    public void writeAndReadEvent_minimal() throws Exception {
        String id = UUID.randomUUID().toString();
        DocumentReference ref = db.collection(TEST_COLL).document(id);

        Map<String, Object> event = new HashMap<>();
        event.put("id", id);
        event.put("title", "Direct Test Event");
        event.put("description", "Created by instrumented test");
        Date now = new Date();
        event.put("registrationOpenDate", now);
        event.put("registrationCloseDate", new Date(now.getTime() + 86_400_000));      // +1 day
        event.put("eventDate", new Date(now.getTime() + 2 * 86_400_000));             // +2 days
        event.put("organizerId", auth.getCurrentUser().getUid());
        event.put("waitlist", Arrays.asList("user-001", "user-002"));
        event.put("inviteList", Collections.emptyList());
        event.put("approvedList", Collections.emptyList());
        event.put("cancelledList", Collections.emptyList());
        event.put("waitlistLimit", 10);
        event.put("capacity", 100);

        // Write
        Tasks.await(ref.set(event));
        createdDocs.add(ref);

        // Read back
        var snapshot = Tasks.await(ref.get());
        assertTrue(snapshot.exists());
        assertEquals("Direct Test Event", snapshot.getString("title"));
        assertEquals(auth.getCurrentUser().getUid(), snapshot.getString("organizerId"));

        @SuppressWarnings("unchecked")
        List<String> waitlist = (List<String>) snapshot.get("waitlist");
        assertNotNull(waitlist);
        assertTrue(waitlist.contains("user-001"));
        assertTrue(waitlist.contains("user-002"));
    }

    @Test
    public void updateInvite_thenApprove() throws Exception {
        String id = UUID.randomUUID().toString();
        DocumentReference ref = db.collection(TEST_COLL).document(id);

        Map<String, Object> base = new HashMap<>();
        base.put("id", id);
        base.put("title", "Invite Flow");
        base.put("description", "Test invite/approve flow");
        base.put("eventDate", new Date(System.currentTimeMillis() + 2 * 86_400_000));
        base.put("registrationOpenDate", new Date());
        base.put("registrationCloseDate", new Date(System.currentTimeMillis() + 86_400_000));
        base.put("organizerId", auth.getCurrentUser().getUid());
        base.put("waitlist", Arrays.asList("u1", "u2", "u3"));
        base.put("inviteList", new ArrayList<String>());
        base.put("approvedList", new ArrayList<String>());
        base.put("cancelledList", new ArrayList<String>());

        Tasks.await(ref.set(base));
        createdDocs.add(ref);

        // Add invite
        Tasks.await(ref.update("inviteList", Arrays.asList("u1", "u2")));

        // Simulate "u1 accepts"
        var snap = Tasks.await(ref.get());
        @SuppressWarnings("unchecked")
        List<String> invite = (List<String>) snap.get("inviteList");
        @SuppressWarnings("unchecked")
        List<String> approved = (List<String>) snap.get("approvedList");
        if (invite == null) invite = new ArrayList<>();
        if (approved == null) approved = new ArrayList<>();
        invite.remove("u1");
        if (!approved.contains("u1")) approved.add("u1");

        Map<String, Object> updates = new HashMap<>();
        updates.put("inviteList", invite);
        updates.put("approvedList", approved);
        Tasks.await(ref.update(updates));

        // Verify
        var snap2 = Tasks.await(ref.get());
        @SuppressWarnings("unchecked")
        List<String> approved2 = (List<String>) snap2.get("approvedList");
        assertTrue(approved2.contains("u1"));
    }


    /**
     * Simple fake implementation of CreateEventUseCase that just records the
     * last Request+Callback instead of touching Firebase.
     */
    private static class FakeCreateEventUseCase implements CreateEventUseCase {
        Request lastRequest;
        Callback lastCallback;

        @Override
        public void execute(Request request, Callback callback) {
            lastRequest = request;
            lastCallback = callback;
        }
    }

    // Reflection helpers to inject/read private fields on the Activity.
    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getPrivateField(Object target, String fieldName) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Happy path:
     * - All fields filled, valid waitlist, image picked
     * - We assert that the Request passed into the use case matches the UI.
     */
    @Test
    public void createEvent_validInput_buildsCorrectRequest() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                // Replace real use case with fake
                setPrivateField(activity, "useCase", fakeUseCase);

                // Prefill dates (so we don't have to open pickers)
                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);

                // Simulate image already picked
                Uri fakeUri = Uri.parse("content://test/image");
                setPrivateField(activity, "imageUri", fakeUri);
            });

            // Fill UI fields via Espresso
            onView(withId(R.id.event_name_entry))
                    .perform(replaceText("Swim Lessons"), closeSoftKeyboard());
            onView(withId(R.id.additional_details_entry))
                    .perform(replaceText("Beginner swim class"), closeSoftKeyboard());

            onView(withId(R.id.limit_waitlist_checkbox)).perform(click());
            onView(withId(R.id.waitlist_size_limit_entry))
                    .perform(replaceText("20"), closeSoftKeyboard());

            onView(withId(R.id.geolocation_checkbox)).perform(click());

            // Click Create
            onView(withId(R.id.create_event_create_button)).perform(click());

            // Inspect the fake use case
            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                assertNotNull("Use case should have been called", fake.lastRequest);
                CreateEventUseCase.Request req = fake.lastRequest;

                assertEquals("Swim Lessons", req.title);
                assertEquals("Beginner swim class", req.description);
                assertNotNull("Event date must not be null", req.eventDate);
                assertNotNull(req.registrationOpenDate);
                assertNotNull(req.registrationCloseDate);
                assertEquals(Integer.valueOf(20), req.waitlistLimit);
                assertTrue(req.requireGeo);
                assertNotNull("Organizer ID should be set", req.organizerId);
                assertEquals(Uri.parse("content://test/image"), req.posterUri);
            });
        }
    }

    /**
     * Invalid waitlist: non-numeric string.
     * Expect: "Digits only" error, and use case NOT called.
     */
    @Test
    public void createEvent_invalidWaitlistNonNumeric_showsErrorAndDoesNotCallUseCase() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                // Minimal valid values for everything else
                EditText nameEt = activity.findViewById(R.id.event_name_entry);
                EditText descEt = activity.findViewById(R.id.additional_details_entry);
                TextView eventDateTv = activity.findViewById(R.id.event_start_date);
                TextView regOpenTv = activity.findViewById(R.id.registration_open_date);
                TextView regCloseTv = activity.findViewById(R.id.registration_deadline_date);

                nameEt.setText("Test Event");
                descEt.setText("Some description");

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());
                eventDateTv.setText(nowStr);
                regOpenTv.setText(nowStr);
                regCloseTv.setText(nowStr);
            });

            onView(withId(R.id.limit_waitlist_checkbox)).perform(click());
            onView(withId(R.id.waitlist_size_limit_entry))
                    .perform(replaceText("abc"), closeSoftKeyboard());
            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                // Should never call execute()
                assertNull("UseCase should not be called for invalid waitlist", fake.lastRequest);

                EditText waitlistEt = activity.findViewById(R.id.waitlist_size_limit_entry);
                assertNotNull(waitlistEt.getError());
                assertEquals("Digits only", waitlistEt.getError().toString());
            });
        }
    }

    /**
     * Edge case: checkbox UNCHECKED.
     * Expect: waitlistLimit in Request is null.
     */
    @Test
    public void createEvent_noWaitlistLimit_checkboxUnchecked_setsWaitlistLimitNull() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                // Minimal valid data
                activity.<EditText>findViewById(R.id.event_name_entry).setText("No Limit Event");
                activity.<EditText>findViewById(R.id.additional_details_entry).setText("Desc");
                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);

                // Make sure checkbox is unchecked
                CheckBox cb = activity.findViewById(R.id.limit_waitlist_checkbox);
                cb.setChecked(false);
            });

            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                assertNotNull("UseCase should be called", fake.lastRequest);
                CreateEventUseCase.Request req = fake.lastRequest;

                assertNull("waitlistLimit should be null when checkbox is unchecked",
                        req.waitlistLimit);
            });
        }
    }

    /**
     * Edge case: no image selected.
     * Expect: posterUri in Request is null, but use case is still called.
     */
    @Test
    public void createEvent_noImageSelected_allowsNullPosterUri() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                activity.<EditText>findViewById(R.id.event_name_entry).setText("No Image Event");
                activity.<EditText>findViewById(R.id.additional_details_entry).setText("Desc");
                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);

                // DO NOT set imageUri here → stays null
            });

            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                assertNotNull(fake.lastRequest);
                assertNull("posterUri should be null when no image selected",
                        fake.lastRequest.posterUri);
            });
        }
    }

    /**
     * Edge case: empty title.
     */
    @Test
    public void createEvent_emptyTitle_showsErrorAndDoesNotCallUseCase() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                // Leave title empty
                activity.<EditText>findViewById(R.id.event_name_entry).setText("");
                activity.<EditText>findViewById(R.id.additional_details_entry).setText("Desc");

                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);
            });

            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                // Expect: validation stops the use case
                assertNull("UseCase should not be called when title is empty", fake.lastRequest);

                EditText nameEt = activity.findViewById(R.id.event_name_entry);
                assertNotNull(nameEt.getError());
                assertEquals("Title required", nameEt.getError().toString());
            });
        }
    }

    /**
     * Edge case: empty description.
     */
    @Test
    public void createEvent_emptyDescription_showsErrorAndDoesNotCallUseCase() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                activity.<EditText>findViewById(R.id.event_name_entry).setText("Title");
                // Leave description empty
                activity.<EditText>findViewById(R.id.additional_details_entry).setText("");

                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);
            });

            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                assertNull("UseCase should not be called when description is empty",
                        fake.lastRequest);

                EditText descEt = activity.findViewById(R.id.additional_details_entry);
                assertNotNull(descEt.getError());
                assertEquals("Description required", descEt.getError().toString());
            });
        }
    }

    /**
     * Edge case: checkbox checked but waitlist limit field left empty.
     */
    @Test
    public void createEvent_checkboxCheckedEmptyLimit_showsErrorAndDoesNotCallUseCase() {
        try (ActivityScenario<EventCreationView> scenario =
                     ActivityScenario.launch(EventCreationView.class)) {

            FakeCreateEventUseCase fakeUseCase = new FakeCreateEventUseCase();

            scenario.onActivity(activity -> {
                setPrivateField(activity, "useCase", fakeUseCase);

                SimpleDateFormat fmt = new SimpleDateFormat("M/d/yy|HH:mm", Locale.US);
                String nowStr = fmt.format(new Date());

                activity.<EditText>findViewById(R.id.event_name_entry).setText("Title");
                activity.<EditText>findViewById(R.id.additional_details_entry).setText("Desc");

                activity.<TextView>findViewById(R.id.event_start_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_open_date).setText(nowStr);
                activity.<TextView>findViewById(R.id.registration_deadline_date).setText(nowStr);

                // Check the checkbox but leave the field empty
                CheckBox cb = activity.findViewById(R.id.limit_waitlist_checkbox);
                cb.setChecked(true);
                activity.<EditText>findViewById(R.id.waitlist_size_limit_entry).setText("");
            });

            onView(withId(R.id.create_event_create_button)).perform(click());

            scenario.onActivity(activity -> {
                FakeCreateEventUseCase fake =
                        (FakeCreateEventUseCase) getPrivateField(activity, "useCase");

                assertNull("UseCase should not be called when checkbox is checked but limit empty",
                        fake.lastRequest);

                EditText waitlistEt = activity.findViewById(R.id.waitlist_size_limit_entry);
                assertNotNull(waitlistEt.getError());
                assertEquals("Digits only", waitlistEt.getError().toString());
            });
        }
    }
}



