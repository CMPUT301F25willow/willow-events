package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.model.Organizer;
import com.example.willowevents.model.User;
import com.example.willowevents.organizer.MainOrganizerView;
/**
 * The ProfileView class is used for the User's profile view. It displays
 * their information, allows them to edit it, and to return to the event view.
 */
public class ProfileView extends AppCompatActivity implements ChangeProfileInfo.EditInfoDialogueListener {
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

                //and display the correct information
                nameText.setText(getCurrName());
                emailText.setText(getCurrEmail());
                phoneText.setText(getCurrPhone());
            }
        });

        //Make the edit button clickable
        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make the popup appear for the user to edit their information
                new ChangeProfileInfo().show(getSupportFragmentManager(), "Edit Info");

            }
        });

        //Buttons that allow the user to go back to the main views
        Button entrantViewButton = findViewById(R.id.entrant_view_button);
        Button organizerViewButton = findViewById(R.id.organizer_view_button);
        entrantViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ENTRANT HOME PAGE (ORGANIZERS CAN SEE ENTRANT HOME PAGE TOO)
                Intent myIntent = new Intent(ProfileView.this, EntrantHomeView.class);
                startActivity(myIntent);

            }
        });
        organizerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser.getUserType() == "organizer" || currentUser.getUserType() == "admin") {
                    Intent myIntent = new Intent(ProfileView.this, MainOrganizerView.class);
                    startActivity(myIntent);
                }
                else {
                    String notifyText = "Cannot access Organizer View. You are not a registered Organizer";
                    Toast toast = Toast.makeText(ProfileView.this, notifyText, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });


    }

    /**
    This method sets the information specified by the user to be the new name, email, and phone
    number. It changes both the text on screen and user's information in the database
     @param newName: This is the new name to set the user's name to
     @param newEmail: This is the new email to set the user's email to
     @param newPhone: This is the new phone number to set the user's phone number to
     */
    public void editInfo(String newName, String newEmail, String newPhone)
    {
        //becomes the actual user's information

        //Update all the texts to the new text
        TextView nameText = findViewById(R.id.nameTextView);
        TextView emailText = findViewById(R.id.emailTextView);
        TextView phoneText = findViewById(R.id.phoneTextView);

        //Set the text to the new information after its been updated
        nameText.setText(newName);
        emailText.setText(newEmail);
        phoneText.setText(newPhone);

        //Update the user's actual information
        currentUser.setName(newName);
        currentUser.setEmail(newEmail);
        currentUser.setPhoneNumber(newPhone);

        // NOTIFY: controller -> database to change info
        userController.updateUserInfo(currentUser);

    }

    //Getters for the name, email, and phone number

    /**
     * Getter for the user's name
     * @return String: the user's name in the database
     */
    public String getCurrName()
    {
        return currentUser.getName();
    }

    /**
     * Getter for the user's email
     * @return String: the user's email in the database
     */
    public String getCurrEmail()
    {
        return currentUser.getEmail();
    }

    /**
     * Getter for the user's phone number
     * @return String: the user's phone number in the database
     */
    public String getCurrPhone()
    {
        return currentUser.getPhoneNumber();
    }
}