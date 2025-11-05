package com.example.willowevents.organizer;

import android.os.Bundle;
import com.example.willowevents.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class is where the organizer modifies the event info.
 * It is largely the same as EventCreationView, but all the
 * options should be auto filled with the current event info.
 */
public class EventModifyView  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);
    }

}
