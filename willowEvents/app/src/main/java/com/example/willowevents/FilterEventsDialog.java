package com.example.willowevents;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilterEventsDialog extends DialogFragment {


    /**
     * Functions activity must implement when a user enteres the filtering preferences
     */
    public interface FilterListener {
        void onFilterConfirmastion(ArrayList<String> preferences, Date from, Date to);
    }


    private FilterListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof FilterListener) {
            listener = (FilterListener) context;
        }
        else {
            throw new RuntimeException("Activity must implement FilterListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // set the fragment layout
        View view = LayoutInflater.from(getContext()).inflate(R.layout.define_tags_popup, null);

        // INTERACTABLES
        ChipGroup tags = view.findViewById(R.id.selected_tags);


        // TODO: ADD OUT OF RANGE  FROM DATE <= START DATE
        Button fromDateButton = view.findViewById(R.id.filter_start_date);
        Button toDateButton = view.findViewById(R.id.filter_end_date);

        final Date[] fromDate = {null};
        final Date[] toDate = {null};

        // USER SELECTS DATES
        fromDateButton.setOnClickListener(v  -> pickDate(date -> {
            fromDate[0] = date;
        }));






        return builder;
    }




}
