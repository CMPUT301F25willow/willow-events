package com.example.willowevents;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class UserListView extends AppCompatActivity {

    private ArrayList<User> users;
    private ListView userView;
    private UserArrayAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);


        users = new ArrayList<User>();

//      REPLACE WITH FIRESTORE SHIT:
        users.add(new Entrant("eventOne"));
        users.add(new Entrant("eventTwo"));
        users.add(new Entrant("eventThree"));
        users.add(new Entrant("eventFour"));
        users.add(new Entrant("eventFive"));
        users.add(new Entrant("eventSix"));
        users.add(new Entrant("eventSeven"));
        users.add(new Entrant("eventEight"));


        userView = findViewById(R.id.user_list);   //find event ListView
        userAdapter = new UserArrayAdapter(this, users); //set array adapter
        userView.setAdapter(userAdapter);     //link array adapter to ListView
    }


}