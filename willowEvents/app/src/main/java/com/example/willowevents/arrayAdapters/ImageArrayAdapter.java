package com.example.willowevents.arrayAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.willowevents.R;
import com.example.willowevents.model.Image;
import com.example.willowevents.model.Notification;

import java.util.ArrayList;

public class ImageArrayAdapter extends ArrayAdapter {
    public ImageArrayAdapter(@NonNull Context context, ArrayList<Image> images){
        super(context, 0, images);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.image_content, parent, false);
        } else {
            view = convertView;
        }
        //get image (serves no purpose)
        Image img = (Image) getItem(position);

        return view;
    }
}
