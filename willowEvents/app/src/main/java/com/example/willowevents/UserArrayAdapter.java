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

    /**
     * This creates a UserArrayAdapter
     * @param context   - Context
     * @param users     - ArrayList<User>
     */
    public UserArrayAdapter(@NonNull Context context, ArrayList<User> users){
        super(context, 0, users);
    }

    /**
     * This returns the view UserArrayAdapter with all necessary details
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return view
     */
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
