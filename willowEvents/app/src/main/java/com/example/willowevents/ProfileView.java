package com.example.willowevents;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.willowevents.controller.UserController;
import com.example.willowevents.entrant.EntrantHomeView;
import com.example.willowevents.entrant.ViewNotifications;
import com.example.willowevents.initialPages.InitialView;
import com.example.willowevents.model.User;
import com.example.willowevents.organizer.MainOrganizerView;
/**
 * The ProfileView class is used for the User's profile view. It displays
 * their information, allows them to edit it, and to return to the event view.
 */
public class ProfileView extends AppCompatActivity implements ChangeProfileInfo.EditInfoDialogueListener, ConfirmProfileDeleteDialog.ConfirmationListener{
    // 1. get the current user
    String deviceID ;

    User currentUser;
    UserController userController;
    TextView userNameText;

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
        userNameText = findViewById(R.id.UserNameText);
        Switch notifToggle = findViewById(R.id.mute_notifs);


        // INIT user controller
        userController = new UserController();
        // GET DEVICE ID
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // SHOW VIEW
        displayUpdatedInfo();

        //Make the edit button clickable
        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Make the popup appear for the user to edit their information
                new ChangeProfileInfo().show(getSupportFragmentManager(), "Edit Info");

            }
        });

        //Make the delete profile button
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConfirmProfileDeleteDialog().show(getSupportFragmentManager(), "deleteProfile");
            }
        });



        //set the default value to their preference:

        //Check to see if they change the toggle:
        notifToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                // Use current view to check whether or not notifcations are muted
                // notify firebase of change

                // set the user that notifications are muted
                currentUser.setHasNotifsMuted(isChecked);

                // update user on the FIREBASE
                userController.updateUserInfo(currentUser);

            }
        });

        //Make the view notifications button
        Button notifButton = findViewById(R.id.view_notif_button);
        notifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to the notification page
                Log.println(Log.VERBOSE, "LILY", "clicked view notifs");

                if(!currentUser.isHasNotifsMuted())
                {
                    Intent myIntent = new Intent(ProfileView.this, ViewNotifications.class);
                    startActivity(myIntent);
                }
                else
                {
                    //when notifications are muted just don't let them access the page \(ovo)/
                    displayNotifsMutedToast();
                }


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
                if (currentUser.getUserType().equals("organizer") || currentUser.getUserType().equals("admin")) {
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
        userNameText.setText(newName);

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

    public void displayNotifsMutedToast()
    {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, "Notifications are muted", duration);
        toast.show();
    }

    /* View method that shows the updated user information from the database
     * and then displays the fields
     */
    private void displayUpdatedInfo() {
        userController.getUser(deviceID, new UserController.OnUserLoaded() {
            @Override
            public void onUserLoaded(User user) {
                currentUser = user;
                // VIEWS AND INTERACTIBLES
                TextView nameText = findViewById(R.id.nameTextView);
                TextView emailText = findViewById(R.id.emailTextView);
                TextView phoneText = findViewById(R.id.phoneTextView);
                Switch notifToggle = findViewById(R.id.mute_notifs);

                // display the correct information
                nameText.setText(getCurrName());
                emailText.setText(getCurrEmail());
                phoneText.setText(getCurrPhone());
                userNameText.setText(getCurrName());
                notifToggle.setChecked(currentUser.isHasNotifsMuted());

            }
        });
    }


    // FOR PROFILE DELETIONS


    /**
     * This function defines the protocol for when user confirms deletion
     *
     */
    @Override
    public void onConfirmDeletion() {
       DeletionProtocolConnector connector = new DeletionProtocolConnector(false);

        // REMOVE USER
        connector.deleteUser(deviceID);


        // finish activity to avoid crash
        finish();

        // go back to initial view
        Intent myIntent = new Intent(ProfileView.this, InitialView.class);
        startActivity(myIntent);

    }


    /**
     * This function defines the protocol for when user CANCELS deletion
     * This is an empty function as nothing will happen and the pop up will simply close
     */
    @Override
    public void onCancelDeletion() {

    }
}