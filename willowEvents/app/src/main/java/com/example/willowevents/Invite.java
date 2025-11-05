package com.example.willowevents;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Invite {
    enum Status {
        INIT,
        INVITED,
        ACCEPTED,
        DECLINED
    }
    Event event;
    Entrant entrant;
    Status status = Status.INIT;
    Invite(Event event, Entrant entrant, Status stat){
        this.event = event;
        this.entrant = entrant;
        switch(stat){
            case INIT:
                this.status = Status.INIT;
                break;
            case INVITED:
                this.status = Status.INVITED;
                break;
            case ACCEPTED:
                this.status = Status.ACCEPTED;
                break;
            case DECLINED:
                this.status = Status.DECLINED;
                break;
        }
    }
}