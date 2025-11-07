package com.example.willowevents;

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

import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;

public class InviteArrayAdapter extends ArrayAdapter {
    Button acceptButton;
    Button declineWithdrawButton;
    TextView inviteMessage;
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

        acceptButton = view.findViewById(R.id.accept_button);
        declineWithdrawButton = view.findViewById(R.id.decline_withdraw_button);
        inviteMessage = view.findViewById(R.id.invite_message);
        assert invite != null;
        String message = "Congratulations USER! You have been invited to " + invite.getEvent().getTitle();
        inviteMessage.setText(message);

        return view;
    }
}
