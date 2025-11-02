package com.example.willowevents;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventCreationView extends AppCompatActivity {

    private EditText sizeLimit;
    private CheckBox doLimit;
    private boolean limiting = false;
    private String waitListLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sizeLimit = findViewById(R.id.waitlist_size_limit_entry);
        doLimit = findViewById(R.id.limit_waitlist_checkbox);

        doLimit.setOnClickListener(view -> {
            limiting = true;
        });

        sizeLimit.setOnClickListener(view -> {
            if(limiting){
                //I will have to implement this somewhere else
                //Need to fix this
                sizeLimit.setFocusable(true);
                sizeLimit.setFocusableInTouchMode(true);
                waitListLimit = sizeLimit.getText().toString();
                //Make sure user input is valid
                if(!ValidateData.containsOnlyDigits(waitListLimit)){
                    //don't let them
                    sizeLimit.setText("");
                }
            }
            else {
                sizeLimit.setFocusable(false);
                sizeLimit.setFocusableInTouchMode(false);
            }
        });
    }
}