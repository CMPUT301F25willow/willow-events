package com.example.willowevents.model;

import java.util.Random;

/**
 * Defines a Lottery class object
 * After being given an event, doLottery will move a select number of entrants from
 * the waitingList to the approvedList
 */
public class Lottery {

    /**
     * Creates Lottery object
     */
    public Lottery(){};

    /**
     * For a max value, a max number of random entrants will be chosen from an event's
     * WaitList and be moved to the event's ApprovedList.
     * max refers to the value of inviteListLimit - size of InviteList - size of ApprovedList
     * @param event - Event
     */
    public void doLottery(Event event){

        int max = event.getInvitelistlimit() - event.getInviteList().size() - event.getApprovedList().size();

        while ( max > 0){
            max-=1;
            int size = event.getWaitlist().size();
            int random = new Random().nextInt(size);
            event.getInviteList().add(event.getWaitlist().get(random));
            event.getInviteList().remove(random);
        }
    }

}
