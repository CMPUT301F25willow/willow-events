package com.example.willowevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserArrayAdapter extends ArrayAdapter {

    public UserArrayAdapter(@NonNull Context context, ArrayList<User> users){
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            // Use layout blueprint, event_content.xml
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_content, parent, false);
        } else {
            view = convertView;
        }
        //get event to grab info from
        User user = (User) getItem(position);

        //get event name and display it in the view
        TextView userName = view.findViewById(R.id.eventName);
        assert user != null;
        userName.setText(user.getName());

        return view;
    }


}
