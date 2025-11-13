package com.example.willowevents.UITests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.content.Context;
import android.provider.Settings;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.willowevents.InitialView;
import com.example.willowevents.R;
import com.example.willowevents.SelectRoleView;
import com.example.willowevents.UserController;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.organizer.MainOrganizerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoginPageTest {
    //private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    UserController userController = new UserController();

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
    public IntentsTestRule<InitialView> intentsTestRule  = new IntentsTestRule<>(InitialView.class);
    @Test
    public void testLoginOrganizer() {
        db.collection("users").document(deviceID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    userController.addNewOrganizerUser(deviceID);
                }
            })
            .addOnFailureListener(e -> {

            });
        onView(ViewMatchers.withId(R.id.login_button)).perform(click());
        intended(hasComponent(MainOrganizerView.class.getName()));
    }
    @Test
    public void testLoginEntrant() {
        db.collection("users").document(deviceID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    userController.addNewEntrantUser(deviceID);
                }
            });
        onView(withId(R.id.login_button)).perform(click());
        intended(hasComponent(EntrantHomeView.class.getName()));
    }
    @Test
    public void testWLoginWithDeviceIdOrganizer() {
        db.collection("users").document(deviceID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    userController.addNewOrganizerUser(deviceID);
                }
            });
        onView(withId(R.id.device_login_button)).perform(click());
        intended(hasComponent(MainOrganizerView.class.getName()));
    }
    @Test
    public void testLoginWithDeviceIdEntrant() {
        db.collection("users").document(deviceID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    userController.addNewEntrantUser(deviceID);
                }
            });
        onView(withId(R.id.device_login_button)).perform(click());
        intended(hasComponent(EntrantHomeView.class.getName()));
    }
    @Test
    public void testSignup() {
        db.collection("users").document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        db.collection("users").document(deviceID).delete();
                    }
                });
        onView(withId(R.id.signup_button)).perform(click());
        intended(hasComponent(SelectRoleView.class.getName()));
    }
    @Test
    public void testSignupWithDeviceId() {
        db.collection("users").document(deviceID)
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    db.collection("users").document(deviceID).delete();
                }
            });
        onView(withId(R.id.device_signup_button)).perform(click());
        intended(hasComponent(SelectRoleView.class.getName()));
    }
}
