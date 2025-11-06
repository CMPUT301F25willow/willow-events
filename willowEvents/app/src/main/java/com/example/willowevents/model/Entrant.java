package com.example.willowevents.model;

import java.util.ArrayList;

public class Entrant extends User {

    public Entrant(String ID,
                   String name,
                   String Email,
                   String phoneNumber,
                   String userType,
                   ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, userType, joinList);
    };
    public Entrant(){}

    public Entrant(String name) {
        super(name);
    }


}
