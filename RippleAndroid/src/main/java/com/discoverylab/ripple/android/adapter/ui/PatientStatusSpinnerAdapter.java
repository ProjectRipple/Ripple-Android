package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.discoverylab.ripple.android.config.Common;

import java.util.List;

/**
 * Custom adapter for spinner with Patient Status options.
 * Created by james on 7/24/14.
 */
public class PatientStatusSpinnerAdapter extends ArrayAdapter<Common.PATIENT_STATUS> implements SpinnerAdapter {
    public PatientStatusSpinnerAdapter(Context context, List<Common.PATIENT_STATUS> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        ((TextView) v).setText(this.getItem(position).getPrintableString());

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);

        ((TextView) v).setText(this.getItem(position).getPrintableString());

        return v;
    }
}