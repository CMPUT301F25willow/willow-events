package com.example.willowevents.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.willowevents.model.Error;
import com.example.willowevents.R;

import java.util.ArrayList;

//Make Error.java and error_content.xml

public class ErrorArrayAdapter extends ArrayAdapter {
    public ErrorArrayAdapter(@NonNull Context context, ArrayList<Error> errors){
        super(context, 0, errors);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            // Use layout blueprint, event_content.xml
            view = LayoutInflater.from(getContext()).inflate(R.layout.error_content, parent, false);
        } else {
            view = convertView;
        }
        //get event to grab info from
        Error error = (Error) getItem(position);

        return view;
    }
}
