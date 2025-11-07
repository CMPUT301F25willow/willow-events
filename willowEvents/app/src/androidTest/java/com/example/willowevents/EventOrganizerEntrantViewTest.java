package com.example.willowevents;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.action.ViewActions;

import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.organizer.EventOrganizerEntrantView;

import java.util.ArrayList;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventOrganizerEntrantViewTest {

    @Rule
    public ActivityScenarioRule<EventOrganizerEntrantView> scenario = new ActivityScenarioRule<EventOrganizerEntrantView>(EventOrganizerEntrantView.class);
    private Event mockEvent(){

        Event event = new Event();

         ArrayList<String> tempList = new ArrayList<>();
         ArrayList<String> entrantlist = new ArrayList<>();

         entrantlist.add("0");
         entrantlist.add("1");
         entrantlist.add("2");
         entrantlist.add("3");

         event.setWaitlist(entrantlist);
        return event;
    }

    @Test
    public void clickOnEvents(){


    }




}
