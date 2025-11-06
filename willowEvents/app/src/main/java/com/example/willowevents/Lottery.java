package com.example.willowevents;

import com.example.willowevents.model.Entrant;

import java.util.ArrayList;
import java.util.Random;

public class Lottery {

    // this is lottery system
    // it's a class cause it's probably easier that way

    private ArrayList<Entrant> selected = new ArrayList<Entrant>();
    public ArrayList<Entrant> doLottery(ArrayList<Entrant> waitlist, int max){


        while ( max > 0){

            max-=1;
            int size = waitlist.size();
            int random = new Random().nextInt(size);
            selected.add(waitlist.get(random));
            waitlist.remove(random);

        }


        return selected;
    }







}
