package com.example.leakwar.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.leakwar.R;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private ArrayList<String> titles;
    private ArrayList<String> descs;

    public CustomArrayAdapter(Activity context, ArrayList<String> titles, ArrayList<String> descs) {
        super(context, R.layout.listviewitems, titles);
        this.context = context;
        this.titles = titles;
        this.descs = descs;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.listviewitems, null, true);

        final TextView title = (TextView) rowView.findViewById(R.id.title_card);
        TextView desc = (TextView) rowView.findViewById(R.id.desc);
        title.setText(this.titles.get(position));
        desc.setText(this.descs.get(position));

        return rowView;
    }
}
