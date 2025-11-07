package com.example.willowevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * This Dialog Fragment allows for a User object to change and update their data
 */
public class ChangeProfileInfo extends DialogFragment {

    interface EditInfoDialogueListener{
        void editInfo(String newName, String newEmail, String newPhone);
        String getCurrName();
        String getCurrEmail();
        String getCurrPhone();
    }

    private EditInfoDialogueListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof EditInfoDialogueListener)
        {
            listener = (EditInfoDialogueListener) context;
        }
        else {
            throw new RuntimeException(context + " must implement EditInfoDialogueListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_change_info_popup, null);

        //view is our full file, so we can get the text from it
        EditText editNameText = view.findViewById(R.id.edit_profile_name_text);
        editNameText.setText(listener.getCurrName());

        EditText editEmailText = view.findViewById(R.id.edit_profile_email_text);
        editEmailText.setText(listener.getCurrEmail());

        EditText editPhoneText = view.findViewById(R.id.edit_profile_phone_text);
        editPhoneText.setText(listener.getCurrPhone());


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Edit Profile")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    String name = editNameText.getText().toString();
                    String email = editEmailText.getText().toString();
                    String phone = editPhoneText.getText().toString();
                    listener.editInfo(name, email, phone);
                })
                .create();
    }
}
