package com.example.willowevents;

import java.util.ArrayList;

public class Entrant extends User {

    public Entrant(String ID,
                   String name,
                   String Email,
                   String phoneNumber,
                   ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, joinList);
    };

    public Entrant(String name) {
        super(name);
    }


}
