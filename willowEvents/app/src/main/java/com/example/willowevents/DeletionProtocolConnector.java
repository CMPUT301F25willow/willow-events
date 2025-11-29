package com.example.willowevents;

import com.example.willowevents.controller.EventController;
import com.example.willowevents.controller.UserController;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.User;

import java.util.ArrayList;

/**
 * This class aggregates Event, User, Notification and Image controllers
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
                // TODO: add test collection initialization
            }
    }


    /**
     * MAIN DELETION FUNCTION
     * This function will defer to another submethod based on the user type (ORGANIZER OR ENTRANT)
     * Protocol of deletion will be done in accordance to the type of user
     * @param userID is a string of the device ID of the user we want to delete
     */
    public void deleteUser(String userID) {

        // get user
        userController.getUser(userID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                String userType = user.getUserType();

                if (userType.equals("organizer")){

                    deleteOrganizer(userID);
                }
                // ENTRANT
                else {
                    deleteEntrant(userID);
                }
            }
        });
    }

    /**
     * Private method meant to delete users from
     * This is to be used in conjuction with deleteEntrant and deleteOrganizer protocols
     * @param userID is the string of the user id from which we want to delete from any events
     */
    private void removeUserRelatedEvents(String userID) {
        // REMOVE USER FROM ANY EVENTS RELATED TO IT
        eventController.generateAllEvents(new EventController.OnEventsGeneration() {
            @Override
            public void onEventsGenerated(ArrayList<Event> events) {

                // remove user from any event list
                for (Event event: events) {
                    // remove user from Waiting list, registered list, cancelled list and invite list
                    eventController.removeUserRegisteredList(event.getId(), userID);
                    eventController.removeUserCancelledList(event.getId(), userID);
                    eventController.removeUserInviteList(event.getId(), userID);
                    eventController.removeUserWaitlist(event.getId(), userID);
                }
            }
        });
    }

    /**
     * This will call the event controller to delete the user ID from any lists in the events in a given firebase
     * Then, the entrant gets deleted
     * @param entrantID is the string of the entrant ID being the device ID given
     */
    public void deleteEntrant(String entrantID) {
        // remove entrant from any related events
        removeUserRelatedEvents(entrantID);

        // SAFE TO DELETE USER
        userController.removeUser(entrantID);
    }


    /**
     * Removes an organizer by protocol
     * 1.
     * This method will remove organizer, as it can also be an entrant, from  any related events it has participated / interacted with
     * @param organizerID is the string of the organizer ID being the device ID given
     */
    public void deleteOrganizer(String organizerID) {

        // remove all users in joinlists of events of Organizers if any
        deleteOrganizerEvents(organizerID);

        // loop through ALL EVENTS of  delete event
        removeUserRelatedEvents(organizerID);

        // an organizer can ALSO BE AN ENTRANT therefore remove user from any events
        userController.removeUser(organizerID);
    }

    /** Iterates through all events to match all events created by organizer.
     * Event will then get deleted
     * @param organizerID
     */
    private void deleteOrganizerEvents(String organizerID) {
        //
        // get all events
        eventController.generateAllEvents(new EventController.OnEventsGeneration() {
            @Override
            public void onEventsGenerated(ArrayList<Event> events) {

                for (Event event: events) {
                    // if organizer-to-delete's event
                    if (organizerID == event.getOrganizerId()) {
                        // delete the event
                        deleteEvent(event.getId());
                    }
                }
            }
        });

    }

    /**
     * Iterates through all user IDs and removes the event ID from the join list of a user if exists
     * to remove the event in any users join
     * @param eventID is the string of the event ID to be deleted
     */
    private void deleteEventForEntrants(String eventID) {

        userController.generateAllUsers(new UserController.OnUsersLoaded() {
            @Override
            public void onUsersLoaded(ArrayList<User> allUsers) {
                for (User user: allUsers) {

                    // remove event ID from join log of user
                    userController.removeEventJoinList(user.getID(), eventID);
                }
            }
        });
    }

    /**
     * removes the event of an organizer by protocol
     *  1. Remove event from the log for any entrant
     *  2. Remove event IMAGE from different collection
     *  3. Then event is safe to remove
     * @param eventID is the String of the ID of an event that will be deleted
     */
    public void deleteEvent(String eventID) {


        // delete event for any entrants
        deleteEventForEntrants(eventID);

        // delete
        eventController.removeEvent(eventID);
    }
}
