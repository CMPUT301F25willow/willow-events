package com.example.willowevents.model;

import java.util.ArrayList;

/**
 * This is a class that defines an Entrant which extends User
 */
public class Entrant extends User {

    /**
     * This creates a new Entrant Object
     * @param ID            - String
     * @param name          - String
     * @param Email         - String
     * @param phoneNumber   - String
     * @param joinList      - ArrayList<String>
     */
    public Entrant(String ID,
                   String name,
                   String Email,
                   String phoneNumber,
                   ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, "entrant", joinList);
    };

    /**
     * This creates an Entrant object with no parameters
     */
    public Entrant(){}

    /**
     * This create an Entrant object with only a name parameter
     * @param name  - String
     */
    public Entrant(String name) {
        super(name);
    }


}
