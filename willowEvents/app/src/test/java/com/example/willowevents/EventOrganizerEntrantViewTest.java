package com.example.willowevents;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Organizer;

import java.util.ArrayList;
public class EventOrganizerEntrantViewTest {

    private Event mockEvent(String name){

        Event event = new Event(name);

         ArrayList<String> tempList = new ArrayList<>();
         ArrayList<Entrant> entrantlist = new ArrayList<>();

         entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", "type", tempList));
         entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", "type", tempList));
         entrantlist.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456","type", tempList));
         entrantlist.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", "type",tempList));

         event.setWaitlist(entrantlist);
        return event;
    }


    private Organizer mockOrganizer(){
        ArrayList<String> tempList = new ArrayList<>();
        Organizer organizer = new Organizer("0", "userOne", "email@gmail.com", "306 123 456", "type", tempList);
        organizer.getCreatedList().add(mockEvent("event1"));
        return organizer;
    }

    





}
