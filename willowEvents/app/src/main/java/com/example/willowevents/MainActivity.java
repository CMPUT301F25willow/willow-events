package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Event> data = new ArrayList<>();
    private EventArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ListView list = findViewById(R.id.eventList);
        adapter = new EventArrayAdapter(this, data);
        list.setAdapter(adapter);
        //createSampleEvent();

        // Real-time updates
        db.collection("events").addSnapshotListener((snap, e) -> {
            if (e != null || snap == null) return;
            data.clear();
            for (DocumentSnapshot d : snap.getDocuments()) {
                Event ev = d.toObject(Event.class);
                if (ev != null) {
                    // set id if not stored
                    if (ev.getId() == null || ev.getId().isEmpty()) ev.setId(d.getId());
                    data.add(ev);
                }
            }
            adapter.notifyDataSetChanged();
        });
        Intent myIntent = new Intent(MainActivity.this, InitialView.class);
        startActivity(myIntent);
    }
}
