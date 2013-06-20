package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import mil.afrl.discoverylab.sate13.rippleandroid.R;

/**
 * Created by Brandon on 6/17/13.
 */
public class PatientLeft extends Fragment implements View.OnClickListener {

    //private ActivityClickInterface aci;

    /**
     * The main dataset that includes all the series that go into a chart.
     */
    private XYMultipleSeriesDataset  mDataset  = new XYMultipleSeriesDataset();
    /**
     * The main renderer that includes all the renderers customizing a chart.
     */
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    /**
     * The most recently added series.
     */
    private XYSeries         mCurrentSeries;
    /**
     * The most recently created renderer, customizing the current series.
     */
    private XYSeriesRenderer mCurrentRenderer;
    /**
     * Button for creating a new series of data.
     */
    private Button           mNewSeries;
    /**
     * Button for adding entered data to the current series.
     */
    private Button           mAdd;
    /**
     * Edit text field for entering the X value of the data to be added.
     */
    private EditText         mX;
    /**
     * Edit text field for entering the Y value of the data to be added.
     */
    private EditText         mY;
    /**
     * The chart view that displays the data.
     */
    private GraphicalView    mChartView;

    private View view;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save the current data, for instance when changing screen orientation
        outState.putSerializable("dataset", mDataset);
        outState.putSerializable("renderer", mRenderer);
        outState.putSerializable("current_series", mCurrentSeries);
        outState.putSerializable("current_renderer", mCurrentRenderer);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        if (savedState != null) {
            // restore the current data, for instance when changing the screen orientation
            mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
            mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
            mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
            mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.patient_left, container, false);
        assert view != null;

        // the top part of the UI components for adding new data points
        mX = (EditText) view.findViewById(R.id.xValue);
        mY = (EditText) view.findViewById(R.id.yValue);
        mAdd = (Button) view.findViewById(R.id.add);

        // set some properties on the main renderer
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(16);
        mRenderer.setLabelsTextSize(16);
        mRenderer.setLegendTextSize(16);
        mRenderer.setMargins(new int[]{32, 32, 32, 32});
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setPointSize(4);
        mRenderer.setShowGrid(true);

        // the button that handles the new series of data creation
        mNewSeries = (Button) view.findViewById(R.id.new_series);
        mNewSeries.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String seriesTitle = "Series " + (mDataset.getSeriesCount() + 1);
                // create a new series of data
                XYSeries series = new XYSeries(seriesTitle);
                mDataset.addSeries(series);
                mCurrentSeries = series;
                // create a new renderer for the new series
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                mRenderer.addSeriesRenderer(renderer);
                // set some renderer properties
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setFillPoints(true);
                renderer.setDisplayChartValues(true);
                renderer.setDisplayChartValuesDistance(10);
                mCurrentRenderer = renderer;
                setSeriesWidgetsEnabled(true);
                mChartView.repaint();
            }
        });

        // The add coordinates button
        mAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double x = 0;
                double y = 0;
                try {
                    x = Double.parseDouble(mX.getText().toString());
                } catch (NumberFormatException e) {
                    mX.requestFocus();
                    return;
                }
                try {
                    y = Double.parseDouble(mY.getText().toString());
                } catch (NumberFormatException e) {
                    mY.requestFocus();
                    return;
                }
                // add a new data point to the current series
                mCurrentSeries.add(x, y);
                mX.setText("");
                mY.setText("");
                mX.requestFocus();
                // repaint the chart such as the newly added point to be visible
                mChartView.repaint();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mChartView == null) {
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.chart);
            mChartView = ChartFactory.getLineChartView(this.getActivity(), mDataset, mRenderer);
            mRenderer.setClickEnabled(false);
            layout.addView(mChartView,
                           new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                         LinearLayout.LayoutParams.MATCH_PARENT));
            boolean enabled = mDataset.getSeriesCount() > 0;
            setSeriesWidgetsEnabled(enabled);
        } else {
            mChartView.repaint();
        }
    }

    /**
     * Enable or disable the add data to series widgets
     *
     * @param enabled the enabled state
     */
    private void setSeriesWidgetsEnabled(boolean enabled) {
        mX.setEnabled(enabled);
        mY.setEnabled(enabled);
        mAdd.setEnabled(enabled);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //aci = (ActivityClickInterface) activity;
    }

    @Override
    public void onClick(View view) {
        //aci.onClickListener(view.getId());
    }
}
