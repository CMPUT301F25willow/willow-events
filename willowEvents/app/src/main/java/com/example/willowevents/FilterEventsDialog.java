package com.example.willowevents;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.willowevents.organizer.EventCreationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FilterEventsDialog extends DialogFragment {


    /**
     * Functions activity must implement when a user enteres the filtering preferences
     */
    public interface FilterListener {
        void onFilterConfirmation(List<String> preferences, Date from, Date to);
    }

    /**
     * Callback function for when date is passed and picked
     */
    public interface DateConsumer {void accept(Date d);}




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

        return builder.setTitle("Filter Events")
                .setView(view)
                .setNegativeButton("Cancel", null)
                // CHECK CHIPS WHEN APPLY IS SELECTED
                .setPositiveButton("Apply", (dialog, which) -> {

                    // ADD WHICH LABELS THE USER TICKED IN THE ARRAY
                    List<String> userPreferences = new ArrayList<>();

                    for (
                            int i = 0;
                            i < tags.getChildCount();
                            i++
                    ) {

                        // each indidual chip check if it is checked
                        Chip chip = (Chip) tags.getChildAt(i);

                        if (chip.isChecked()) {
                            userPreferences.add(chip.getText().toString());
                        }

                    }
                    listener.onFilterConfirmation(userPreferences, fromDate[0], toDate[0]);
                }).create()
                ;
    }

    private void pickDate(DateConsumer consumer) {
        final Calendar cal = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(requireContext(),
                (view, year, month, day) -> {
                    cal.set(Calendar.YEAR, year);
                    cal.set(Calendar.MONTH, month);
                    cal.set(Calendar.DAY_OF_MONTH, day);
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }




}
