package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import java.util.List;

/**
 * Adapter for a Spinner of colored views
 * Created by james on 7/17/14.
 */
public class ColorSpinnerAdapter extends ArrayAdapter<Integer> implements SpinnerAdapter {
    public ColorSpinnerAdapter(Context context, List<Integer> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        // set background
        v.setBackgroundColor(getItem(position));
        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        // set background
        v.setBackgroundColor(getItem(position));
        return v;
    }

}
