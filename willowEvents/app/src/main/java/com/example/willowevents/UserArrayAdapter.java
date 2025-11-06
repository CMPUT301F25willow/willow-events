package com.example.willowevents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.willowevents.model.User;

import java.util.ArrayList;

/**
 * This ArrayAdapter manages how user info is displayed in lists
 */
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
        //get text items
        TextView userName = view.findViewById(R.id.title_text);
        TextView userPhoneNumber = view.findViewById(R.id.user_phone_number);
        TextView userEmail = view.findViewById(R.id.user_email);
        //set text items
        userName.setText(user.getName());
        userPhoneNumber.setText(user.getPhoneNumber());
        userEmail.setText(user.getEmail());

        return view;
    }


}
