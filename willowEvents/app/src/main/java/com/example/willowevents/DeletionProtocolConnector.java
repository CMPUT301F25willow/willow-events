package com.example.willowevents;

import com.example.willowevents.controller.EventController;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

/**
 * This class aggregates Event, User and Image controllers
 * Defines protocol for when an event and user and image gets deleted as there exists many
 * inter-connected dependencies between each respective class
 */
public class DeletionProtocolConnector {

    // Needs:
    // - Event controller
    // - User controller
    // - Image controller

    EventController eventController;
    UserController userController;


    /**
     *
     * @param test_database is TRUE if you want to use the test collection from firebase
     *                      else it is FALSE if you want to use the regular collection from firebase
     */
    public DeletionProtocolConnector(boolean test_database) {
            if (test_database) {
                eventController = new EventController();
                userController = new UserController();
            }
            else {
                eventController = new EventController();
                userController = new UserController();
            }
    }


    /**
     * This will call the event controller to delete the user ID from any lists in the events in a given firebase
     * Then, the entrant gets deleted
     * @param entrantID is the string of the entrant ID being the device ID given
     */
    public void deleteEntrant(String entrantID) {

        // REMOVE USER FROM ANY EVENTS RELATED TO IT
        eventController.generateAllEvents(new EventController.OnEventsGeneration() {
            @Override
            public void onEventsGenerated(ArrayList<Event> events) {

                // remove user from any event list
                for (Event event: events) {
                    // remove user from Waiting list, registered list, cancelled list and invite list
                    eventController.removeUserRegisteredList(event.getId(), entrantID);
                    eventController.removeUserCancelledList(event.getId(), entrantID);
                    eventController.removeUserInviteList(event.getId(), entrantID);
                    eventController.removeUserWaitlist(event.getId(), entrantID);
                }
            }
        });

        // SAFE TO DELETE USER
        userController.removeUser(entrantID);

    }
    public void deleteOrganizer(String organizerID) {
        // loop through ALL EVENTS of the organizer and delete event
    }
    public void deleteEvent(String eventID) {
        // for each user, get user, remove event ID from the user joinLists / signUP lists


    }
}
