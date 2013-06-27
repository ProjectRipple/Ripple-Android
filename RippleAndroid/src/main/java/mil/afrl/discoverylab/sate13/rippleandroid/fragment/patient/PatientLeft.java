package mil.afrl.discoverylab.sate13.rippleandroid.fragment.patient;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import mil.afrl.discoverylab.sate13.rippleandroid.R;
import mil.afrl.discoverylab.sate13.rippleandroid.adapter.DatabaseAdapter;

/**
 * Created by Brandon on 6/17/13.
 */
public class PatientLeft extends Fragment implements View.OnClickListener {

    //private ActivityClickInterface aci;
    private boolean addedSeries = false;

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

        // set some properties on the main renderer
        mRenderer.setApplyBackgroundColor(true);
        mRenderer.setBackgroundColor(Color.argb(255, 238, 237, 240));
        mRenderer.setLabelsColor(Color.BLACK);
        mRenderer.setMarginsColor(Color.WHITE);
        //mRenderer.setAxisTitleTextSize(16);
        //mRenderer.setChartTitleTextSize(16);
        //mRenderer.setLabelsTextSize(16);
        //mRenderer.setLegendTextSize(16);
        //mRenderer.setMargins(new int[]{32, 32, 32, 32});
        mRenderer.setYLabelsPadding((float)10);
        mRenderer.setZoomButtonsVisible(false);
        mRenderer.setShowGrid(true);
        mRenderer.setGridColor(Color.GRAY);
        mRenderer.setAxesColor(Color.BLACK);
        mRenderer.setXLabelsColor(Color.BLACK);
        mRenderer.setYLabelsColor(0, Color.BLACK);


        ImageView tagview = (ImageView) view.findViewById(R.id.tagview);
        assert tagview != null;
        tagview.setClickable(true);
        tagview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.finger_paint_dialog);
                dialog.setTitle(" Draw Something Friend ");

                // set the custom dialog components - text, image and button
                Button dialogButton = (Button) dialog.findViewById(R.id.fingerbuttonok);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        return view;
    }

    private void setupSeries() {
        String seriesTitle = "";

        // create a new series of data
        XYSeries series = new XYSeries(seriesTitle);
        mDataset.addSeries(series);
        mCurrentSeries = series;

        // create a new renderer for the new series
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        // set some renderer properties
        renderer.setShowLegendItem(false);
        renderer.setColor(Color.RED);
        renderer.setDisplayChartValues(false);
        //renderer.setDisplayChartValuesDistance(16);

        mRenderer.addSeriesRenderer(renderer);
        mCurrentRenderer = renderer;
        mChartView.repaint();
    }

    private void addDataPoint(double x, double y) {
        // add a new data point to the current series
        mCurrentSeries.add(x, y);
        // repaint the chart such as the newly added point to be visible
        mChartView.repaint();
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

            if (!addedSeries) {
                setupSeries();
                DatabaseAdapter.getInstance().getVitalXY("localhost", "ECG", mCurrentSeries);
                addedSeries = true;
            }
            mChartView.repaint();
        } else {
            mChartView.repaint();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onClick(View view) {
    }
}
