package com.example.willowevents.UITests;

import static org.junit.Assert.*;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginPageTest {
    @Rule
    public ActivityScenarioRule<InitialView> scenario = new ActivityScenarioRule<InitialView>(InitialView.class);
    @Test
    public void testLogin() {
        onView(withId(R.id.login_button)).perform(click());
    }
    @Test
    public void testLoginWithDeviceId() {
        onView(withId(R.id.device_login_button)).perform(click());
    }
    @Test
    public void testSignup() {
        onView(withId(R.id.signup_button)).perform(click());
    }
    @Test
    public void testSignupWithDeviceId() {
        onView(withId(R.id.device_signup_button)).perform(click());
    }
}
