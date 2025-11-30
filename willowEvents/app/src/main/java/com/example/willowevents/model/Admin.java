package com.example.willowevents.model;

import java.util.ArrayList;

public class Admin extends User {

    /**
     * This creates a new Entrant Object
     * @param ID            - String
     * @param name          - String
     * @param Email         - String
     * @param phoneNumber   - String
     * @param joinList      - ArrayList<String>
     */
    public Admin(String ID,
                   String name,
                   String Email,
                   String phoneNumber,
                   ArrayList<String> joinList) {
        super(ID, name, Email, phoneNumber, "admin", joinList);
    };

    public Admin() {
        super();
    }

}
