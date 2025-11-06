package com.example.willowevents.organizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.willowevents.EventArrayAdapter;
import com.example.willowevents.R;
import com.example.willowevents.model.Entrant;
import com.example.willowevents.model.Event;

import java.util.ArrayList;

public class MainOrganizerView extends AppCompatActivity {
    ListView eventView;
    EventArrayAdapter eventAdapter;
    ArrayList<Event> events;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_organizer_view);

        events = new ArrayList<Event>();
//      REPLACE WITH FIRESTORE SHIT:
        //events.add(new Event("eventOne"));
        //events.add(new Event("eventTwo"));
       // events.add(new Event("eventThree"));
       // events.add(new Event("eventFour"));
       // events.add(new Event("eventFive"));

        Event event = addMockEvent();
        events.add(event);
        events.add(event);
        events.add(event);
        events.add(event);

        // for testing only
        ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, events);

        eventView = findViewById(R.id.eventList);   //find event ListView
        //eventAdapter = new EventArrayAdapter(this, events); //set array adapter
        //eventView.setAdapter(eventAdapter);     //link array adapter to ListView

        eventView.setAdapter(arrayAdapter);



        eventView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent myIntent = new Intent(MainOrganizerView.this,EventOrganizerEntrantView.class);
                //share data with new activity
                myIntent.putExtra("Key", 10 );
                startActivity(myIntent);

            }


        });



    }


    public Event addMockEvent(){

        Event event = new Event("EventOne");
        ArrayList<String> tempList = new ArrayList<>();
        ArrayList<Entrant> entrantlist = new ArrayList<>();

        entrantlist.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
        entrantlist.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));

        event.setWaitlist(entrantlist);
        return event;
    }



}