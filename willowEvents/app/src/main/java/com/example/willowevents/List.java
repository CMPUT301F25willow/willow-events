package com.example.willowevents;

import java.util.ArrayList;

public class List {

    private ArrayList<Entrant> entrantList;

    // Question, what would be better?
    // One class for all lists waitlist, cancelled list, approved list
    // or  multiple classes for each list?
    // they use the same methods, so the one list class should be good?

    public void addEntrant(Entrant entrant){
        this.entrantList.add(entrant);
    }

    public void removeEntrant(Entrant entrant){
        this.entrantList.remove(entrant);
    }

}
