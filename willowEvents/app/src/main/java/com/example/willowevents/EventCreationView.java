package com.example.willowevents;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormatSymbols;

public class EventCreationView extends AppCompatActivity {

    private String dateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Event event = new Event("Name");

        Button create = findViewById(R.id.create_event_create_button);

        Button eventStartButton = findViewById(R.id.event_start_date_button);
        Button registerDeadlineButton = findViewById(R.id.registration_open_button);
        Button registerOpenButton = findViewById(R.id.registration_deadline_button);
        Button cancelButton = findViewById(R.id.create_event_cancel_button);


        TextView eventStart = findViewById(R.id.event_start_date);
        TextView registerDeadline = findViewById(R.id.registration_deadline_date);
        TextView registerOpen = findViewById(R.id.registration_open_date);

        int hr =0 ;
        int min =0;


        eventStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog(hr, min);
                event.setEventDate(dateTime);
                eventStart.setText(dateTime);
            }
        });

        registerDeadlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog(hr, min);
                event.setRegistrationDeadline(dateTime);
                registerDeadline.setText(dateTime);
            }
        });

        registerOpenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateDialog(hr, min);
                event.setRegistrationOpen(dateTime);
                registerOpen.setText(dateTime);
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // add to organizer createdList

            }
        });



    }


    private void dateDialog(int hr, int min){

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                dateTime = new DateFormatSymbols().getMonths()[month-1] + "/" + day + "/"+ year + "|";
                timeDialog(hr, min);
            }
        }, 2025, 11, 1);
        dialog.show();
    }

    private void timeDialog(int hr, int min) {

        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourofDay, int minute) {
                dateTime = dateTime + hourofDay+":"+minute;
            }

        }, hr, min, false);
        dialog.show();

    }




}