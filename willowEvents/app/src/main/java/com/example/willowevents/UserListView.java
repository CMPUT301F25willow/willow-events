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

public class UserListView extends AppCompatActivity {

    private ArrayList<User> users;
    private ListView userView;
    private UserArrayAdapter userAdapter;
    private Button close;
    private TextView numberOfEntrants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_view);

        //Get elements
        close = findViewById(R.id.close_button);
        numberOfEntrants =findViewById(R.id.number_people_on_waitlist);

        //Go back on close
        close.setOnClickListener(view -> {
            Intent myIntent = new Intent(UserListView.this, EventOrganizerEntrantView.class);
            startActivity(myIntent);
        });

        //FIRESTORE STUFF HERE
        users = new ArrayList<User>();

        ArrayList<String> tempList = new ArrayList<>();

//      REPLACE WITH FIRESTORE SHIT:
        users.add(new Entrant("0", "userOne", "email@gmail.com", "306 123 456", tempList));
        users.add(new Entrant("1", "userTwo", "email@gmail.com", "306 123 456", tempList));
        users.add(new Entrant("2", "userThree", "email@gmail.com", "306 123 456", tempList));
        users.add(new Entrant("3", "userFour", "email@gmail.com", "306 123 456", tempList));
        users.add(new Entrant("userFive"));
        users.add(new Entrant("userSix"));
        users.add(new Entrant("userSeven"));

        int size = users.size();
        String numberEntrantsMessage = "Number of users on waitlist : " + size;
        numberOfEntrants.setText(numberEntrantsMessage);

        userView = findViewById(R.id.user_list);   //find event ListView
        userAdapter = new UserArrayAdapter(this, users); //set array adapter
        userView.setAdapter(userAdapter);     //link array adapter to ListView
    }


}