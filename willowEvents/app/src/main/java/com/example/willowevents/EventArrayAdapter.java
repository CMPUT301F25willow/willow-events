package com.example.willowevents;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.willowevents.model.Event;

import java.util.ArrayList;

/**
 * This ArrayAdapter manages how events are displayed in lists
 */
public class EventArrayAdapter extends ArrayAdapter {
    public EventArrayAdapter(@NonNull Context context, ArrayList<Event> events){
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            // Use layout blueprint, event_content.xml
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_list_content, parent, false);
        } else {
            view = convertView;
        }
        //get event to grab info from
        Event event = (Event) getItem(position);

        //get event name and display it in the view
        TextView eventName = view.findViewById(R.id.eventName);
        eventName.setText(event.getTitle());

        return view;
    }
}

