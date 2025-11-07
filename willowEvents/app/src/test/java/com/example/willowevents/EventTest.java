package com.example.willowevents;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

public class EventTest {

    private Event mockEvent(){

        Event event = new Event("EventOne");
        /**
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Entrant> entrantlist = new ArrayList<>();

        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));
`
        event.setWaitlist(entrantlist);
         */
        return event;
    }

    @Test
    public void addEntrantTest(){

        Event event = mockEvent();
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Entrant> entrantlist = new ArrayList<>();
        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        event.setWaitlist(entrantlist);
        assertEquals(1,event.getWaitlist().size());
        event.getWaitlist().remove(0);
        assertEquals(0,event.getWaitlist().size());
    }

    @Test
    public void notSameEntrantTest(){
        Event event = mockEvent();
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Entrant> entrantlist = new ArrayList<>();
        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        event.setWaitlist(entrantlist);
        assertEquals(2,event.getWaitlist().size());
        assertNotEquals(event.getWaitlist().get(0), event.getWaitlist().get(1));
    }

    @Test
    public void checkNullTest(){
        Event event = mockEvent();
        assertNull(null,event.getWaitlist());
    }

}
