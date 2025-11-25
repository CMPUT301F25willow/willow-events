package com.example.willowevents.arrayAdapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.willowevents.admin.AdminHomeView;
import com.example.willowevents.model.Event;
import com.example.willowevents.model.Invite;

import java.util.ArrayList;

public class InviteArrayAdapter extends ArrayAdapter {
    public InviteArrayAdapter(@NonNull Context context, ArrayList<Invite> events){
        super(context, 0, events);
    }
}
