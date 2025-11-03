package com.example.willowevents;

import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class EntrantHomeView extends AppCompatActivity {
    //Stole from taylor's MainOrganizerView
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_entrant_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Stole from taylor's MainOrganizerView
        events = new ArrayList<Event>();
//      REPLACE WITH FIRESTORE SHIT:
        /*events.add(new Event("eventOne"));
        events.add(new Event("eventTwo"));
        events.add(new Event("eventThree"));
        events.add(new Event("eventFour"));
        events.add(new Event("eventFive"));*/


        eventView = findViewById(R.id.eventList);   //find event ListView
        eventAdapter = new EventArrayAdapter(this, events); //set array adapter
        eventView.setAdapter(eventAdapter);     //link array adapter to ListView
    }
}