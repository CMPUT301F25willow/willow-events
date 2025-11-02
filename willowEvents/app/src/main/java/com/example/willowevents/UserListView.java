package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class UserListView extends AppCompatActivity {

    private ArrayList<User> users;
    private ListView userView;
    private UserArrayAdapter userAdapter;
    private Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        //Go back on close
        close = findViewById(R.id.close_button);
        close.setOnClickListener(view -> {
            Intent myIntent = new Intent(UserListView.this, EventOrganizerEntrantView.class);
            startActivity(myIntent);
        });

        //FIRESTORE STUFF HERE
        users = new ArrayList<User>();
//        ArrayList<String> tempJoinList = new ArrayList<String>();
//        users.add((User) new Entrant("Alberto"));
//        users.add((User) new Entrant("Bertrand"));
//        users.add((User) new Entrant("Cornelius"));
//        users.add(new User("1", "Bernadice", "B@gmail.com", "123456", tempJoinList));
//        users.add(new User("2", "Cornelius", "C@gmail.com", "123456", tempJoinList));
        userView = findViewById(R.id.user_list);   //find event ListView
        userAdapter = new UserArrayAdapter(this, users); //set array adapter
        userView.setAdapter(userAdapter);     //link array adapter to ListView
    }


}