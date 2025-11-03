package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Objects;

public class UserListView extends AppCompatActivity {

    private ListView userView;
    private UserArrayAdapter userAdapter;
    private Button close;
    private TextView numberOfEntrants;
    private ArrayList<User> waitlistUsers;
    private ArrayList<User> invitedUsers;
    private ArrayList<User> enrolledUsers;
    private ArrayList<User> cancelledUsers;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            //get value from extras using the key
            type = extras.getString("Type");
        }

        //REPLACE WITH FIRESTORE STUFF
        waitlistUsers = new ArrayList<User>();
        invitedUsers = new ArrayList<User>();
        enrolledUsers = new ArrayList<User>();
        cancelledUsers = new ArrayList<User>();

        ArrayList<String> tempList = new ArrayList<>();

        waitlistUsers.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        waitlistUsers.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        waitlistUsers.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
        waitlistUsers.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));

        invitedUsers.add(new Entrant("0", "Alfredo", "email@gmail.com", "306 123 456", tempList));
        invitedUsers.add(new Entrant("1", "Bernadice", "email@gmail.com", "306 123 456", tempList));
        invitedUsers.add(new Entrant("2", "Claudelia", "email@gmail.com", "306 123 456", tempList));
        invitedUsers.add(new Entrant("3", "Domingo", "email@gmail.com", "306 123 456", tempList));

        enrolledUsers.add(new Entrant("0", "Albert", "email@gmail.com", "306 123 456", tempList));
        enrolledUsers.add(new Entrant("1", "Bertrand", "email@gmail.com", "306 123 456", tempList));
        enrolledUsers.add(new Entrant("2", "Cornelius", "email@gmail.com", "306 123 456", tempList));
        enrolledUsers.add(new Entrant("3", "Daedalus", "email@gmail.com", "306 123 456", tempList));

        cancelledUsers.add(new Entrant("0", "Aurora", "email@gmail.com", "306 123 456", tempList));
        cancelledUsers.add(new Entrant("1", "Belle", "email@gmail.com", "306 123 456", tempList));
        cancelledUsers.add(new Entrant("2", "Cinderella", "email@gmail.com", "306 123 456", tempList));
        cancelledUsers.add(new Entrant("3", "Diana", "email@gmail.com", "306 123 456", tempList));

        //Get elements
        close = findViewById(R.id.close_button);
        numberOfEntrants =findViewById(R.id.number_people_on_waitlist);

        //Go back on close
        close.setOnClickListener(view -> {
            Intent myIntent = new Intent(UserListView.this, EventOrganizerEntrantView.class);
            startActivity(myIntent);
        });

        userView = findViewById(R.id.user_list);
        int size;
        if(Objects.equals(type, "waitlist")){
            size = waitlistUsers.size();
            userAdapter = new UserArrayAdapter(this, waitlistUsers);
        }
        else if(Objects.equals(type, "invited")){
            size = invitedUsers.size();
            userAdapter = new UserArrayAdapter(this, invitedUsers);
        }
        else if (Objects.equals(type, "enrolled")){
            size = enrolledUsers.size();
            userAdapter = new UserArrayAdapter(this, enrolledUsers);
        }
        else if (Objects.equals(type, "cancelled")){
            size = cancelledUsers.size();
            userAdapter = new UserArrayAdapter(this, cancelledUsers);
        }
        else {
            //smth bad happened
            size = 0;
        }
        String numberEntrantsMessage = "Number of users on waitlist : " + size;
        numberOfEntrants.setText(numberEntrantsMessage);

        userView.setAdapter(userAdapter);     //link array adapter to ListView
    }


}