package com.discoverylab.ripple.android.adapter.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.discoverylab.ripple.android.R;

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

        // Set custom background
        v.setBackgroundResource(R.drawable.color_back_with_border);

        // set center color of view
        LayerDrawable background = (LayerDrawable) v.getBackground();
        ((GradientDrawable)background.getDrawable(1)).setColor(getItem(position));

        // hide text by matching background & set to nothing
        ((TextView)v).setTextColor(getItem(position));
        ((TextView)v).setText("");


        return v;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        // set custom background
        v.setBackgroundResource(R.drawable.color_back_with_border);

        // set center color of view
        LayerDrawable background = (LayerDrawable) v.getBackground();
        ((GradientDrawable)background.getDrawable(1)).setColor(getItem(position));


        // hide text by matching background & set to nothing
        ((TextView)v).setTextColor(getItem(position));
        ((TextView)v).setText("");
        return v;
    }

}
