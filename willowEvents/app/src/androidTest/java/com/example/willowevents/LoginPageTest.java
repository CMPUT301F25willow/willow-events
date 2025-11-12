package com.example.willowevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.provider.Settings;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.organizer.MainOrganizerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {
    //private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    private Map<String, Object> addUser(String type){
        Map<String, Object> user = new HashMap<>();
        user.put("createdList", null);
        user.put("email", null);
        user.put("id", deviceID);
        user.put("joinList", null);
        user.put("name", null);
        user.put("phoneNumber", null);
        user.put("userType", type);
        return user;
    }
    private void clearCollection(String col){
        db.collection(col).get().addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                db.collection(col).document(doc.getId()).delete();
            }
        });
    }

    @Rule
    public ActivityScenarioRule<InitialView> scenario = new ActivityScenarioRule<InitialView>(InitialView.class);
    @Test
    public void testLoginOrganizer() {
        //Set up emulator db
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        db.collection("users").add(addUser("organizer"));

        onView(ViewMatchers.withId(R.id.login_button)).perform(click());
        intended(hasComponent(MainOrganizerView.class.getName()));
    }
    @Test
    public void testLoginEntrant() {
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        db.collection("users").add(addUser("entrant"));

        onView(withId(R.id.login_button)).perform(click());
        intended(hasComponent(EntrantHomeView.class.getName()));
    }
    @Test
    public void testLoginWithDeviceIdOrganizer() {
        //Add the user to the database
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        db.collection("users").add(addUser("organizer"));
        //click button
        onView(withId(R.id.device_login_button)).perform(click());
        //assert switched to appropriate activity
        intended(hasComponent(MainOrganizerView.class.getName()));
        //make sure added to database???
    }
    @Test
    public void testLoginWithDeviceIdEntrant() {
        //Add the user to the database
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        db.collection("users").add(addUser("entrant"));
        //click button
        onView(withId(R.id.device_login_button)).perform(click());
        //assert switched to appropriate activity
        intended(hasComponent(EntrantHomeView.class.getName()));
        //make sure added to database???
    }
    @Test
    public void testSignup() {
        //Set up db
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        //click button
        onView(withId(R.id.signup_button)).perform(click());
        //make sure transitioned to the correct page
        intended(hasComponent(SelectRoleView.class.getName()));
        //make sure added to database???
    }
    @Test
    public void testSignupWithDeviceId() {
        //set up db
        db.useEmulator("10.0.2.2", 8080);
        clearCollection("users");
        //click button
        onView(withId(R.id.device_signup_button)).perform(click());
        //make sure transitioned to the correct page
        intended(hasComponent(SelectRoleView.class.getName()));
        //make sure added to database???
    }
}
