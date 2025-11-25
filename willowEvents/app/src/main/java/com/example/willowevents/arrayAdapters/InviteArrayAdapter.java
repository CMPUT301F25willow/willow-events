package com.example.willowevents.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import com.example.willowevents.R;
import com.example.willowevents.model.Invite;

public class InviteArrayAdapter extends ArrayAdapter {
    Button acceptButton;
    Button declineWithdrawButton;
    TextView inviteMessage;
    TextView inviteStatusMessage;
    public InviteArrayAdapter(@NonNull Context context, ArrayList<Invite> invites){
        super(context, 0, invites);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            // Use layout blueprint, event_content.xml
            view = LayoutInflater.from(getContext()).inflate(R.layout.invite_content, parent, false);
        } else {
            view = convertView;
        }
        //get event to grab info from
        Invite invite = (Invite) getItem(position);

        inviteMessage = view.findViewById(R.id.invite_message);
        inviteStatusMessage = view.findViewById(R.id.invite_status_text);
        assert invite != null;
        String message = "Congratulations " + invite.getUser().getName() + "! You have been invited to " + invite.getEvent().getTitle();
        inviteMessage.setText(message);
        inviteStatusMessage.setText("Current Status: " + invite.getStatus());

        return view;
    }
}