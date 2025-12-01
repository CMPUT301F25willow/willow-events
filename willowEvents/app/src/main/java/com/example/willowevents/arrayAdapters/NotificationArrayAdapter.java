package com.example.willowevents.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.example.willowevents.R;
import com.example.willowevents.model.Notification;

public class NotificationArrayAdapter extends ArrayAdapter {
    public NotificationArrayAdapter(@NonNull Context context, ArrayList<Notification> notifications){
        super(context, 0, notifications);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            // Use layout blueprint, event_content.xml
            view = LayoutInflater.from(getContext()).inflate(R.layout.notification_list_content, parent, false);
        } else {
            view = convertView;
        }
        //get event to grab info from
        Notification noti = (Notification) getItem(position);

        TextView notiMessage = view.findViewById(R.id.notification_message);
        TextView eventName = view.findViewById((R.id.event_name));
        assert noti != null;
        notiMessage.setText(noti.getNotificationMessage());
        eventName.setText(noti.getEventName());
        return view;
    }
}
