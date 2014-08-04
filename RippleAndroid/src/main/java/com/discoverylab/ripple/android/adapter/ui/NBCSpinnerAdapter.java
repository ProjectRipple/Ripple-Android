package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;
import com.discoverylab.ripple.android.config.Common;

import java.util.List;

/**
 * Custom adapter for spinner with NBC contamination options.
 * Created by james on 7/24/14.
 */
public class NBCSpinnerAdapter extends ArrayAdapter<Common.NBC_CONTAMINATION_OPTIONS> implements SpinnerAdapter {
    public NBCSpinnerAdapter(Context context, List<Common.NBC_CONTAMINATION_OPTIONS> objects) {
        super(context, R.layout.custom_simple_spinner_item, objects);
        setDropDownViewResource(R.layout.custom_simple_spinner_dropdown_item);
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
