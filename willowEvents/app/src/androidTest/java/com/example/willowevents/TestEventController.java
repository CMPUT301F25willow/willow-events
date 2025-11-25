package com.example.willowevents;
import static org.junit.Assert.*;

import com.example.willowevents.controller.EventController;
import com.example.willowevents.model.Event;

import org.junit.After;
import org.junit.Test;


/**
 * Tests the event controller on
 * - Updating
 * - Event DNE
 * - Event Exists
 */
public class TestEventController {

    String eventID = "TEST_EVENT";
    String eventName = "TEST_TITLE";


    /**
     * After each test is conducted, mock event must be REMOVED from the database
     */
    @After
    public void addInitialEvent() {
        EventController eventController = new EventController();

        eventController.removeEvent(eventID);
    }

    /**
     * Creates a fixed event with empty placeholders
     * @return Event object
     */
    private Event mockEvent() {
        Event mockEvent = new Event();
        mockEvent.setTitle(eventName);
        mockEvent.setId(eventID);
        return mockEvent;
    }


    /**
     * Tests if mock event stores essential attributes of event in database
     */
    @Test
    public void testEventUpdates() {
        // add event
        Event myEvent = mockEvent();
        EventController controller = new EventController();
        // initial add to database
        controller.updateEventInfo(myEvent);

        // check that it exists
        controller.generateOneEvent(eventID, new EventController.OnEventGeneration() {
                    @Override
                    public void onEventGenerated(Event event) {
                        assertEquals(event.getId(), eventID);
                        assertEquals(event.getTitle(), eventName);
                    }
        });


        // update event class
        String title = "Specific title";
        myEvent.setTitle(title);

        // udpate event in database
        controller.updateEventInfo(myEvent);

        // get event and test if event was correctly updated
        controller.generateOneEvent(eventID, new EventController.OnEventGeneration() {
            @Override
            public void onEventGenerated(Event event) {
                Event capturedEvent = event;

                // this event should not exist in the array
                assertNotNull(capturedEvent);
                // this event should hav the updated title
                assertEquals(capturedEvent.getTitle(), title);

            }
        });
    }


    /**
     * Tests if mock event does NOT recognize existence of event ID when it doesn't exist
     */
    @Test
    public void testEventDNE() {
        EventController eventController = new EventController();

        eventController.generateOneEvent(eventID, new EventController.OnEventGeneration() {
            @Override
            public void onEventGenerated(Event event) {
                assertNull(event);
            }
        });
    }
}
