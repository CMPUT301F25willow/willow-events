package com.example.willowevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


/**
 * This is a pop up class that will let a user confirm the deletion of their profile
 */
public class ConfirmProfileDeleteDialog extends DialogFragment {


    /**
     * Functions Main must implement when a user confirms or cancels profile deletion
     */
    public interface ConfirmationListener {
        void onConfirmDeletion();
        void onCancelDeletion();
    }

    private ConfirmationListener listener;

    @Override
    public void onAttach(@NonNull Context context) {

        // attach to current screen
        super.onAttach(context);


        // confirm that the Activity does in fact implement the
        if (context instanceof ConfirmationListener) {
            listener = (ConfirmationListener) context;
        }
        else {
            throw new RuntimeException( context + " must implement ConfirmationListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        return builder.setTitle("Delete Profile").setMessage(
                "Are you sure you want to delete your profile? All information will be deleted permanently."
        ).setNegativeButton("CANCEL", (dialog, which) ->
        {
            listener.onCancelDeletion();
        }
        ).setPositiveButton("CONFIRM", (dialog, which) -> {
            listener.onConfirmDeletion();
        }).create();
    }
}
