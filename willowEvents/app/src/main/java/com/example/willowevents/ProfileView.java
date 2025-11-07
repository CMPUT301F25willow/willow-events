package com.example.willowevents;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileView extends AppCompatActivity implements ChangeProfileInfo.EditInfoDialogueListener {

    //This variable keeps track of if the edit button has been hit, in other words if the
    // profile text is editable

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

        //We want to make the edit button clickable
        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //change the mode to be editable
                new ChangeProfileInfo().show(getSupportFragmentManager(), "Edit Info");

            }
        });
        TextView nameText = findViewById(R.id.nameTextView);
        TextView emailText = findViewById(R.id.emailTextView);
        TextView phoneText = findViewById(R.id.phoneTextView);

        //TODO: this will work once the getters are set to connect to firestore
        //and display the correct information
        nameText.setText(getCurrName());
        emailText.setText(getCurrEmail());
        phoneText.setText(getCurrPhone());

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

    }

    //Getters for the name, email, and phone number
    public String getCurrName()
    {
        //TODO: connect with firestore to return the real values here
        return "John";
    }
    public String getCurrEmail()
    {
        //TODO: connect with firestore to return the real values here
        return "john@john.com";
    }
    public String getCurrPhone()
    {
        //TODO: connect with firestore to return the real values here
        return "780-123-456";
    }
}