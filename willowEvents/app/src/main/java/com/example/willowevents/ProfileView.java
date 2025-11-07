package com.example.willowevents;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.model.User;

public class ProfileView extends AppCompatActivity implements ChangeProfileInfo.EditInfoDialogueListener {

    //This variable keeps track of if the edit button has been hit, in other words if the
    // profile text is editable

    // 1. get the current user
    String deviceID ;

    User currentUser;
    UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // VIEW ITEMS
        TextView nameText = findViewById(R.id.nameTextView);
        TextView emailText = findViewById(R.id.emailTextView);
        TextView phoneText = findViewById(R.id.phoneTextView);




        // INIT user controller
        userController = new UserController();
        // GET DEVICE ID
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // GET CURRENT USER FROM DATABASE
        userController.getUser(deviceID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;

                //TODO: this will work once the getters are set to connect to firestore
                //and display the correct information
                nameText.setText(getCurrName());
                emailText.setText(getCurrEmail());
                phoneText.setText(getCurrPhone());
            }
        });

        //We want to make the edit button clickable
        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change the mode to be editable
                new ChangeProfileInfo().show(getSupportFragmentManager(), "Edit Info");

            }
        });

        Button entrantViewButton = findViewById(R.id.entrant_view_button);
        Button organizerViewButton = findViewById(R.id.organizer_view_button);
        entrantViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: switch to EntrantHomeView
            }
        });
        organizerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TODO: switch to MainOrganizerView
            }
        });


    }

    /**
    This method sets the information specified by the user to be the new name, email, and phone
    number.
     @param newName: This is the new name to set the user's name to
     @param newEmail: This is the new email to set the user's email to
     @param newPhone: This is the new phone number to set the user's phone number to
     */
    public void editInfo(String newName, String newEmail, String newPhone)
    {
        //TODO: IMPLEMENT FIRESTORE CONNECTIONS -- make it so the new information passed through
        //becomes the actual user's information

        //Update all the texts to the new text
        TextView nameText = findViewById(R.id.nameTextView);
        TextView emailText = findViewById(R.id.emailTextView);
        TextView phoneText = findViewById(R.id.phoneTextView);

        //Set the text to the new information after its been updated
        nameText.setText(newName);
        emailText.setText(newEmail);
        phoneText.setText(newPhone);

        currentUser.setName(newName);
        currentUser.setEmail(newEmail);
        currentUser.setPhoneNumber(newPhone);

        // NOTIFY: controller -> database to change info
        userController.updateUserInfo(currentUser);

    }

    //Getters for the name, email, and phone number
    public String getCurrName()
    {
        //TODO: connect with firestore to return the real values here
        return currentUser.getName();
    }
    public String getCurrEmail()
    {
        //TODO: connect with firestore to return the real values here
        return currentUser.getEmail();
    }
    public String getCurrPhone()
    {
        //TODO: connect with firestore to return the real values here
        return currentUser.getPhoneNumber();
    }
}